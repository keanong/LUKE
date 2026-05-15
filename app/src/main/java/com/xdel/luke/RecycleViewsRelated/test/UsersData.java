package com.xdel.luke.RecycleViewsRelated.test;

import java.util.ArrayList;
import java.util.List;

public class UsersData {

    /*private List<User> usersList = new ArrayList<User>() {
        {
            add(new User(0, "", "", "OPS"));
            add(new User(1, "Frederick Hoffman", "https://randomuser.me/api/portraits/men/52.jpg", "OPS"));
            add(new User(2, "Calvin Young", "https://randomuser.me/api/portraits/men/78.jpg", "OPS"));
            add(new User(3, "Jeanette Reid", "https://randomuser.me/api/portraits/women/37.jpg", "OPS"));
            add(new User(4, "Flenn Wilson", "https://randomuser.me/api/portraits/men/40.jpg", "OPS"));
            add(new User(5, "Martin Holland", "https://randomuser.me/api/portraits/men/0.jpg", "OPS"));
            add(new User(6, "", "", "GC"));
            add(new User(7, "Jeanette Simmmons", "https://randomuser.me/api/portraits/women/3.jpg", "GC"));
            add(new User(8, "Wallace Lambert", "https://randomuser.me/api/portraits/men/53.jpg", "GC"));
            add(new User(9, "Andy Clark", "https://randomuser.me/api/portraits/men/68.jpg", "GC"));
            add(new User(10, "olivia obrien", "https://randomuser.me/api/portraits/women/93.jpg", "GC"));
            add(new User(11, "Debbie Bennett", "https://randomuser.me/api/portraits/women/34.jpg", "GC"));
            add(new User(12, "", "", "VRP"));
            add(new User(13, "Bernice Lawson", "https://randomuser.me/api/portraits/women/20.jpg", "VRP"));
            add(new User(14, "Camila Elliott", "https://randomuser.me/api/portraits/women/60.jpg", "VRP"));
            add(new User(15, "Gerald Webb", "https://randomuser.me/api/portraits/men/55.jpg", "VRP"));
            add(new User(16, "Russell Hart", "https://randomuser.me/api/portraits/men/18.jpg", "VRP"));
            add(new User(17, "Joyce Mccoy", "https://randomuser.me/api/portraits/women/82.jpg", "VRP"));
            add(new User(18, "Kean Ong", "", "VRP"));
            add(new User(19, "Daryl Banks", "https://randomuser.me/api/portraits/men/4.jpg", "VRP"));
            add(new User(20, "Veronica Vargas", "https://randomuser.me/api/portraits/women/14.jpg", "VRP"));
            add(new User(21, "Natalie Jacobs", "https://randomuser.me/api/portraits/women/0.jpg", "VRP"));
            add(new User(22, "Beverly Kennedy", "https://randomuser.me/api/portraits/women/30.jpg", "VRP"));
        }
    };*/

    private List<User> usersList = new ArrayList<User>() {
        {
            add(new User(0, "", "", "OPS", "t"));
            add(new User(1, "Frederick Hoffman", "https://randomuser.me/api/portraits/men/52.jpg", "OPS", "f"));
            add(new User(2, "Calvin Young", "https://randomuser.me/api/portraits/men/78.jpg", "OPS", "f"));
            add(new User(3, "Jeanette Reid", "https://randomuser.me/api/portraits/women/37.jpg", "OPS", "f"));
            add(new User(4, "Flenn Wilson", "https://randomuser.me/api/portraits/men/40.jpg", "OPS", "f"));
            add(new User(5, "Martin Holland", "https://randomuser.me/api/portraits/men/0.jpg", "OPS", "f"));
            add(new User(6, "", "", "VRP", "t"));
            add(new User(7, "Jeanette Simmmons", "https://randomuser.me/api/portraits/women/3.jpg", "VRP", "f"));
            add(new User(8, "Wallace Lambert", "https://randomuser.me/api/portraits/men/53.jpg", "VRP", "f"));
            add(new User(9, "Kean Ong", "https://randomuser.me/api/portraits/men/68.jpg", "VRP", "f"));
            add(new User(10, "olivia obrien", "https://randomuser.me/api/portraits/women/93.jpg", "VRP", "f"));
            add(new User(11, "Debbie Bennett", "https://randomuser.me/api/portraits/women/34.jpg", "VRP", "f"));
            add(new User(12, "Bernice Lawson", "https://randomuser.me/api/portraits/women/20.jpg", "VRP", "f"));
            add(new User(13, "Camila Elliott", "https://randomuser.me/api/portraits/women/60.jpg", "VRP", "f"));
            add(new User(14, "Gerald Webb", "https://randomuser.me/api/portraits/men/55.jpg", "VRP", "f"));
            add(new User(15, "Russell Hart", "https://randomuser.me/api/portraits/men/18.jpg", "VRP", "f"));
            add(new User(16, "Joyce Mccoy", "https://randomuser.me/api/portraits/women/82.jpg", "VRP", "f"));
            add(new User(17, "Andy Clark", "", "VRP", "f"));
            add(new User(18, "Daryl Banks", "https://randomuser.me/api/portraits/men/4.jpg", "VRP", "f"));
            add(new User(19, "Veronica Vargas", "https://randomuser.me/api/portraits/women/14.jpg", "VRP", "f"));
            add(new User(20, "Natalie Jacobs", "https://randomuser.me/api/portraits/women/0.jpg", "VRP", "f"));
            add(new User(21, "Beverly Kennedy", "https://randomuser.me/api/portraits/women/30.jpg", "VRP", "f"));
        }
    };

    public List<User> getUsersList() {
        return usersList;
    }

}
