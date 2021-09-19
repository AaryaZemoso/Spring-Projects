package com.chatroom.app.service.user;

import com.chatroom.app.convertor.UserConvertor;
import com.chatroom.app.dao.authorities.AuthoritiesDAO;
import com.chatroom.app.dao.user.UserRepository;
import com.chatroom.app.dto.user.UserResponseDTO;
import com.chatroom.app.entity.Authorities;
import com.chatroom.app.entity.Roles;
import com.chatroom.app.entity.User;
import com.chatroom.app.exception.user.UserNotFoundException;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthoritiesDAO authoritiesDAO;

    @Autowired
    private UserConvertor userConvertor;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EntityManager entityManager;

    @Override
    public List<UserResponseDTO> findAll() {
        return userRepository.findAll()
                .stream()
                .map(userConvertor::getResponse)
                .collect(Collectors.toList());
    }

    @Override
    public User findById(int id) {

        Optional<User> user = userRepository.findById(id);

        if(!user.isPresent())
            throw new UserNotFoundException("User with " + id + " not found");

        return user.get();
    }

    @Override
    public void save(User user) {

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
        authoritiesDAO.save(new Authorities(0, user.getEmail(), Roles.USER.getRole()));
    }

    @Override
    public void deleteById(int id) {

        User user = this.findById(id);

        if(user != null)
        {
            authoritiesDAO.delete(user.getEmail());
            userRepository.deleteById(id);
        }
        else
            throw new UserNotFoundException("User with id " + id + " not found");
    }

    @Override
    public User findByEmail(String email) {

        Session session = entityManager.unwrap(Session.class);
        Query<User> query = session.createQuery("from User where email = :email");

        query.setParameter("email", email);

        return query.getSingleResult();
    }
}
