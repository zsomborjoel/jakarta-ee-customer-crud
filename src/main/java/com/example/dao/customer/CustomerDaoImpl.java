package com.example.dao.customer;

import com.example.config.DataSourceFactory;
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

public class CustomerDaoImpl implements CustomerDao {
    @Override
    public Optional<Customer> find(final Integer id) throws SQLException {
        final String sql = """
                    SELECT id, name, email, phone, gender
                    FROM customer
                    WHERE id=?""";

        ResultSet resultSet;
        final Connection connection = DataSourceFactory.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);) {
            preparedStatement.setString(1, String.valueOf(id));
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
        final String sql = """
                    SELECT id, name, email, phone, gender
                    FROM customer""";

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
        return false;
    }

    @Override
    public boolean update(final Customer customer) throws SQLException {
        return false;
    }

    @Override
    public boolean delete(final Customer customer) throws SQLException {
        return false;
    }
}
