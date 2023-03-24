package com.example.controller;

import com.example.config.ObjectMapperFactory;
import com.example.dao.customer.CustomerDao;
import com.example.dao.customer.CustomerDaoImpl;
import com.example.model.Customer;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = {"/customers", "/customers/find", "/customers/update",
                           "/customers/delete", "/customers/insert"})
public class CustomerController extends HttpServlet {

    private CustomerDao customerDao = CustomerDaoImpl.getInstance();
    private ObjectMapper objectMapper = ObjectMapperFactory.getInstance();

    private final Logger LOGGER = Logger.getLogger(CustomerController.class.getName());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOGGER.info("Action: " + req.getServletPath());
        final String[] pathParts = req.getServletPath().split("/");
        final String path = pathParts[pathParts.length - 1];

        try {
            if (path.equals("insert")) {
                insert(req, resp);
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOGGER.info("Action: " + req.getServletPath());
        final String[] pathParts = req.getServletPath().split("/");
        final String path = pathParts[pathParts.length - 1];

        try {
            if (path.equals("delete")) {
                delete(req, resp);
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOGGER.info("Action: " + req.getServletPath());
        final String[] pathParts = req.getServletPath().split("/");
        final String path = pathParts[pathParts.length - 1];

        try {
            if (path.equals("update")) {
                update(req, resp);
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOGGER.info("Action: " + req.getServletPath());
        final String[] pathParts = req.getServletPath().split("/");
        final String path = pathParts[pathParts.length - 1];

        try {
            if (path.equals("find")) {
                find(req, resp);
            } else {
                findAll(req, resp);
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void find(HttpServletRequest req, HttpServletResponse resp) throws SQLException, RuntimeException {
        final String id = req.getParameter("id");
        final Optional<Customer> customer = customerDao.find(Integer.parseInt(id));
        customer.ifPresent(c -> {
            try {
                final String jsonString = objectMapper.writeValueAsString(c);
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                resp.getWriter().write(jsonString);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void findAll(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        final List<Customer> customers = customerDao.findAll();
        final String jsonString = objectMapper.writeValueAsString(customers);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(jsonString);
    }

    private void insert(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        final String body = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        final Customer customer = objectMapper.readValue(body, Customer.class);
        final boolean saved = customerDao.save(customer);
        if (saved) {
            resp.setStatus(HttpServletResponse.SC_OK);
            return;
        } 
        
        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    private void update(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        final String body = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        final Customer customer = objectMapper.readValue(body, Customer.class);
        final boolean updated = customerDao.update(customer);
        if (updated) {
            resp.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    private void delete(HttpServletRequest req, HttpServletResponse resp) throws SQLException {
        final int id = Integer.parseInt(req.getParameter("id"));
        final boolean deleted = customerDao.delete(new Customer(id));
        if (deleted) {
            resp.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}
