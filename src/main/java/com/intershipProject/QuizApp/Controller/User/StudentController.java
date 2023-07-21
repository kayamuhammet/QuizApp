package com.intershipProject.QuizApp.Controller.User;

import com.intershipProject.QuizApp.Model.Announcement;
import com.intershipProject.QuizApp.Model.Course;
import com.intershipProject.QuizApp.Model.User;
import com.intershipProject.QuizApp.Model.UserQuiz;
import com.intershipProject.QuizApp.Repository.AnnouncementRepo;
import com.intershipProject.QuizApp.Repository.CourseRepo;
import com.intershipProject.QuizApp.Repository.UserQuizRepo;
import com.intershipProject.QuizApp.Repository.UserRepo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class StudentController {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private CourseRepo courseRepo;

    @Autowired
    private UserQuizRepo userQuizRepo;

    @Autowired
    private AnnouncementRepo announcementRepo;

    @GetMapping(value = "/profile")
    public ModelAndView profileInfo(HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("view/html/user/profile");

        HttpSession session = request.getSession();
        String sessionId = (String) session.getAttribute("sessionId");
        User loggedInUser = userRepo.findBySessionId(sessionId);

        List<UserQuiz> userScores = userQuizRepo.findByUserId(loggedInUser.getId());
        List<Course> courses = courseRepo.findAll();

        modelAndView.addObject("userScores",userScores);
        modelAndView.addObject("courses", courses);
        modelAndView.addObject("user", loggedInUser);

        return modelAndView;
    }

    @GetMapping(value = "/user-dashboard")
    public ModelAndView dashboard(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("view/html/user/dashboard");

        //get course
        List<Course> last3courses = courseRepo.findTop3ByOrderByIdDesc();

        HttpSession session = request.getSession();
        String sessionId = (String) session.getAttribute("sessionId");
        User loggedInUser = userRepo.findBySessionId(sessionId);

        // Her kurs için ait skorları çek ve ilgili kursların altında döngülemek üzere modele ekle
        Map<Course, UserQuiz> latestCourseScoresMap  = new HashMap<>();
        for (Course course : last3courses) {
            UserQuiz latestUserScore = userQuizRepo.findTopByUserIdAndCourseIdOrderByScoreDesc(loggedInUser.getId(), course.getId());
            latestCourseScoresMap.put(course, latestUserScore);
        }

        //get announcement
        List<Announcement> latestAnnouncements  = announcementRepo.findTop5ByOrderByCreatedDateDesc();

        //Add to modelAndView
        modelAndView.addObject("announcements", latestAnnouncements);
        modelAndView.addObject("user", loggedInUser);
        modelAndView.addObject("courses", last3courses);
        modelAndView.addObject("latestCourseScoresMap",latestCourseScoresMap);

        return modelAndView;
    }
    @GetMapping(value = "/user-quiz")
    public ModelAndView quiz(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("view/html/user/show-quiz");
        List<Course> courses = courseRepo.findAll();

        HttpSession session = request.getSession();
        String sessionId = (String) session.getAttribute("sessionId");
        User loggedInUser = userRepo.findBySessionId(sessionId);

        modelAndView.addObject("user", loggedInUser);
        modelAndView.addObject("courses", courses);

        return modelAndView;
    }
    @GetMapping(value = "/user-announcement")
    public ModelAndView announcement(HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("view/html/user/announcement");

        HttpSession session = request.getSession();
        String sessionId = (String) session.getAttribute("sessionId");
        User loggedInUser = userRepo.findBySessionId(sessionId);

        modelAndView.addObject("user", loggedInUser);

        List<Announcement> announcements = announcementRepo.findAll();
        modelAndView.addObject("announcements", announcements);

        return modelAndView;
    }


}