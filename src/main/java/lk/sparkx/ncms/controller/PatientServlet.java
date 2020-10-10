package lk.sparkx.ncms.controller;

import lk.sparkx.ncms.dao.Bed;
import lk.sparkx.ncms.dao.Hospital;
import lk.sparkx.ncms.dao.Patient;
import lk.sparkx.ncms.db.DBConnectionPool;
import lk.sparkx.ncms.repository.BedRepository;
import lk.sparkx.ncms.repository.HospitalRepository;
import lk.sparkx.ncms.repository.PatientRepository;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "PatientServlet")
public class PatientServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        String firstName = req.getParameter("firstName");
        String lastName = req.getParameter("lastName");
        String district = req.getParameter("district");
        int locationX = Integer.parseInt(req.getParameter("locationX"));
        int locationY = Integer.parseInt(req.getParameter("locationY"));
        String severityLevel = req.getParameter("severityLevel");
        String gender = req.getParameter("gender");
        String contact = req.getParameter("contact");
        String email = req.getParameter("email");
        int age = Integer.parseInt(req.getParameter("age"));

        java.util.Date dateAdmit = new java.util.Date();
        Date admitDate = new Date(dateAdmit.getTime());

        String admittedBy = req.getParameter("admittedBy");

        java.util.Date dateDischarged = new java.util.Date();
        Date dischargeDate = new Date(dateDischarged.getTime());

        String dischargedBy = req.getParameter("dischargedBy");


        Patient patient = new Patient();
        patient.setId(id);
        patient.setFirstName(firstName);
        patient.setLastName(lastName);
        patient.setDistrict(district);
        patient.setLocationX(locationX);
        patient.setLocationY(locationY);
        patient.setSeverityLevel(severityLevel);
        patient.setGender(gender);
        patient.setContact(contact);
        patient.setEmail(email);
        patient.setAge(age);
        patient.setAdmitDate(admitDate);
        patient.setAdmittedBy(admittedBy);
        patient.setDischargeDate(dischargeDate);
        patient.setDischargedBy(dischargedBy);

        PatientRepository patientRepository = new PatientRepository();
        String patientRegistered = patientRepository.registerPatient(patient);

        if(patientRegistered.equals("SUCCESS"))   //On success, you can display a message to user on Home page
        {
            System.out.println("Success");
            //request.getRequestDispatcher("/PatientDetails.jsp").forward(request, response);
        }
        else   //On Failure, display a meaningful message to the User.
        {
            System.out.println("Failed");
            //request.setAttribute("errMessage", patientRegistered);
            //request.getRequestDispatcher("/PatientRegister.jsp").forward(request, response);
        }

        try {
            patientRepository.registerPatient(patient);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");

        Connection connection = null;
        PreparedStatement statement = null;
        PreparedStatement statement2 = null;
        int result = 0;

        try {
            connection = DBConnectionPool.getInstance().getConnection();
            //PreparedStatement statement;
            ResultSet resultSet2;

            statement = connection.prepareStatement("SELECT * FROM patient WHERE id=?");
            statement.setString(1, id);
            System.out.println(statement);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                //String id = resultSet.getString("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String district = resultSet.getString("district");
                int locationX = resultSet.getInt("location_x");
                int locationY = resultSet.getInt("location_y");
                String severityLevel = resultSet.getString("severity_level");
                String gender = resultSet.getString("gender");
                String contact = resultSet.getString("contact");
                String email = resultSet.getString("email");
                int age = resultSet.getInt("age");
                Date admitDate = resultSet.getDate("admit_date");
                String admittedBy = resultSet.getString("admitted_by");
                Date dischargeDate = resultSet.getDate("discharge_date");
                String dischargedBy = resultSet.getString("discharged_by");


                PrintWriter printWriter = resp.getWriter();

                printWriter.println("Id: " + id);
                printWriter.println("First name: " + firstName);
                printWriter.println("Last name: " + lastName);
                printWriter.println("District: " + district);
                printWriter.println("Location_X: " + locationX);
                printWriter.println("Location_Y: " + locationY);
                printWriter.println("Severity Level: " + severityLevel);
                printWriter.println("Gender: " + gender);
                printWriter.println("Contact: " + contact);
                printWriter.println("Email: " + email);
                printWriter.println("Age: " + age);
                printWriter.println("Admit Date: " + admitDate);
                printWriter.println("Admitted By: " + admittedBy);
                printWriter.println("Discharge Date: " + dischargeDate);
                printWriter.println("Discharged By: " + dischargedBy);
                System.out.println("doGet patient success");

                HospitalRepository hospitalRepository = new HospitalRepository();
                String nearestHospital = hospitalRepository.assignHospital(locationX, locationY);
                System.out.println("Nearest hospital: " + nearestHospital);

                Bed bed = new Bed();

                int bedId = bed.allocateBed(nearestHospital, id);
                System.out.println("Bed ID: " + bedId);

                if(bedId == 0){
                    statement2 = connection.prepareStatement("SELECT distinct id FROM hospital where id !='" + nearestHospital + "'");
                    System.out.println(statement2);
                    resultSet2 = statement2.executeQuery();
                    String hosId ="";
                    if(resultSet2.next()) {
                        hosId = resultSet2.getString("id");
                        System.out.println(hosId);
                    }
                    bed.allocateBed(hosId, id);

                }

            }

            connection.close();

        } catch (Exception ignored) {

        }
    }
    }

