package com.example.dao.customer;

import com.example.config.DataSourceFactory;
import com.example.controller.CustomerController;
import com.example.model.Customer;

import javax.xml.transform.Result;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class CustomerDaoImpl implements CustomerDao {

    private final Logger LOGGER = Logger.getLogger(CustomerDaoImpl.class.getName());

    private CustomerDaoImpl() {}

    private static class SingletonHelper {
        private static final CustomerDaoImpl INSTANCE = new CustomerDaoImpl();
    }

    public static CustomerDaoImpl getInstance() {
        return SingletonHelper.INSTANCE;
    }

    @Override
    public Optional<Customer> find(final Integer id) throws SQLException {
        final String sql = "SELECT id, name, email, phone, gender\n" +
                           "FROM customer\n" +
                           "WHERE id=?";

        ResultSet resultSet;
        final Connection connection = DataSourceFactory.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);) {
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
        }

        if (resultSet.next()) {
            return Optional.of(new Customer(
                    resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getString("email"),
                    resultSet.getString("phone"),
                    resultSet.getString("gender")
            ));
        }

        return Optional.empty();
    }

    @Override
    public List<Customer> findAll() throws SQLException {
        final List<Customer> customers = new ArrayList<>();
        final String sql = "SELECT id, name, email, phone, gender\n" +
                           "FROM customer";

        ResultSet resultSet;
        final Connection connection = DataSourceFactory.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);) {
            resultSet = preparedStatement.executeQuery();
        }

        while (resultSet.next()) {
            customers.add(new Customer(
                    resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getString("email"),
                    resultSet.getString("phone"),
                    resultSet.getString("gender")
            ));
        }

        return customers;
    }

    @Override
    public boolean save(final Customer customer) throws SQLException {
        final String sql = "INSERT INTO customer (name, email, phone, gender)\n" +
                           "VALUES (?, ?, ?, ?)";

        final Connection connection = DataSourceFactory.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);) {
            preparedStatement.setString(1, customer.getName());
            preparedStatement.setString(2, customer.getEmail());
            preparedStatement.setString(3, customer.getPhone());
            preparedStatement.setString(4, customer.getGender());
            return preparedStatement.execute();
        }
    }

    @Override
    public boolean update(final Customer customer) throws SQLException {
        final String sql = "UPDATE customer SET\n" +
                           "    name = ?,\n" +
                           "    email = ?,\n" +
                           "    phone = ?,\n" +
                           "    gender = ?\n" +
                           "WHERE id = ?";

        final Connection connection = DataSourceFactory.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);) {
            preparedStatement.setString(1, customer.getName());
            preparedStatement.setString(2, customer.getEmail());
            preparedStatement.setString(3, customer.getPhone());
            preparedStatement.setString(4, customer.getGender());
            preparedStatement.setInt(5, customer.getId());
            return preparedStatement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(final Customer customer) throws SQLException {
        final String sql = "DELETE from customer WHERE id = ?";

        final Connection connection = DataSourceFactory.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);) {
            preparedStatement.setInt(1, customer.getId());
            return preparedStatement.executeUpdate() > 0;
        }
    }
}
