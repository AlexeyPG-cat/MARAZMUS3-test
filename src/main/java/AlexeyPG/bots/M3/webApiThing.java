package AlexeyPG.bots.M3;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class webApiThing {
    public static void start() throws LifecycleException {
        //Took a lot of time to find a good tutorial to steal it from
        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir("temp");
        tomcat.setPort(20207); //TODO make port configurable

        String contextPath = "/";
        String docBase = new File(".").getAbsolutePath();

        Context context = tomcat.addContext(contextPath, docBase);

        HttpServlet servlet = new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                PrintWriter writer = resp.getWriter();
                resp.setHeader("Access-Control-Allow-Origin", "*");
                //I know that's not how it works. I don't know how to make it work how it supposes to work. Well it still works so who cares
                if(req.getParameter("question") != null) switch (req.getParameter("question")){
                    case "PIOnline": writer.println(statistics.getOnline("860944840853291008"));break;
                    case "PITotal": writer.println(statistics.getTotal("860944840853291008"));break;
                    case "PIInGame": writer.println(statistics.getInGame("860944840853291008"));break;
                    default: writer.println("Unknown");break;
                }
            }
        };

        String servletName = "Servlet1";
        String urlPattern = "/MARAZMUS";

        tomcat.addServlet(contextPath, servletName, servlet);
        context.addServletMappingDecoded(urlPattern, servletName);
        tomcat.start();
        System.out.println(tomcat.getConnector().getState()); //It doesn't work without this line

        tomcat.getServer().await();
    }
}
