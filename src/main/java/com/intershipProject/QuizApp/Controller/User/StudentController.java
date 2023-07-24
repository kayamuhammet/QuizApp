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

import java.util.ArrayList;
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

        HttpSession session = request.getSession();
        String sessionId = (String) session.getAttribute("sessionId");
        User loggedInUser = userRepo.findBySessionId(sessionId);

        // Get the last 3 UserQuiz objects for the logged-in user
        List<UserQuiz> last3UserQuizzes = userQuizRepo.findTop3ByUserIdOrderByScoreDesc(loggedInUser.getId());

//        // Create a list to hold the corresponding courses for the last 3 UserQuiz objects
//        List<Course> last3courses = new ArrayList<>();
//        for (UserQuiz userQuiz : last3UserQuizzes) {
//            Course course = userQuiz.getCourse();
//            last3courses.add(course);
//        }

        // Pull the scores for each UserQuiz and add them to the model to loop under their respective UserQuiz
        Map<UserQuiz, Course> latestUserQuizCoursesMap = new HashMap<>();
        for (UserQuiz userQuiz : last3UserQuizzes) {
            Course course = userQuiz.getCourse();
            latestUserQuizCoursesMap.put(userQuiz, course);
        }

        //get announcement
        List<Announcement> latestAnnouncements = announcementRepo.findTop5ByOrderByCreatedDateDesc();

        //Add to modelAndView
        modelAndView.addObject("announcements", latestAnnouncements);
        modelAndView.addObject("user", loggedInUser);
        //modelAndView.addObject("userQuizzes", last3UserQuizzes);
        modelAndView.addObject("latestUserQuizCoursesMap", latestUserQuizCoursesMap);

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