/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package hospitt;

/**
 *
 * @author ysr
 */
import java.util.Timer;
import java.util.TimerTask;

public class TimerExample {
    public static void main(String[] args) {
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                System.out.println("Task executed at: " + System.currentTimeMillis());
                // Your time-sensitive code here
            }
        };

        timer.schedule(task, 2000, 5000); // Delay 2 seconds, then repeat every 5 seconds

        //Schedule only once after 3 seconds
        timer.schedule(task, 3000);
        //It is not necessary to shutdown timer but you can use timer.cancel()
    }
}