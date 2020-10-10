package lk.sparkx.ncms.controller;

import lk.sparkx.ncms.dao.User;
import lk.sparkx.ncms.repository.UserRepository;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet (name = "UserServlet")
public class UserServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String name = req.getParameter("name");
        boolean isMoh = Boolean.parseBoolean(req.getParameter("isMoh"));
        boolean isHospital = Boolean.parseBoolean(req.getParameter("isHospital"));

        User user = new User();
        user.setUserName(username);
        user.setPassword(password);
        user.setName(name);
        user.setMoh(isMoh);
        user.setHospital(isHospital);

        UserRepository userRepository = new UserRepository();
        String doctorRegistered = userRepository.viewStatistics(user);

        if (doctorRegistered.equals("SUCCESS"))   //On success, you can display a message to user on Home page
        {
            System.out.println("Success");
        } else   //On Failure, display a meaningful message to the User.
        {
            System.out.println("Failed");
        }

        try {
            userRepository.viewStatistics(user);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
