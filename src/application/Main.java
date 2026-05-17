package application;



import application.apps.BaseApp;

import application.apps.UniversityApplication;



public class Main{

    /**
     * starts main logic (language->auth->menu->logout) by using {@link UniversityApplication}
     * UniversityApplication inherits {@link BaseApp}, 
     * but it only renders menu based on who the user is.
     */
    public static void main(String[] args){
        UniversityApplication.changeLanguage();

        UniversityApplication.authenticate();

        if(UniversityApplication.isAuthenticated())
            UniversityApplication.start();

        UniversityApplication.kill();

    }





}


