package app.termora.ftpd;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ftpserver.DataConnectionConfigurationFactory;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FtpServerApplication {
    public static void main(String[] args) throws Exception {
        final Path path = Paths.get("data");
        if (Files.notExists(path)) {
            Files.createDirectories(path);
        }

        final FtpServerFactory ftpServerFactory = new FtpServerFactory();
        final ListenerFactory listenerFactory = new ListenerFactory();
        final PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
        final DataConnectionConfigurationFactory dataConnConfigFactory = new DataConnectionConfigurationFactory();

        final String pasvPorts = System.getenv("PASV_PORTS");
        if (StringUtils.isNotBlank(pasvPorts)) {
            dataConnConfigFactory.setPassivePorts(pasvPorts);
        }

        final String pasvAddress = System.getenv("PASV_ADDRESS");
        if (StringUtils.isNotBlank(pasvAddress)) {
            dataConnConfigFactory.setPassiveExternalAddress(pasvAddress);
        }

        listenerFactory.setDataConnectionConfiguration(dataConnConfigFactory.createDataConnectionConfiguration());
        listenerFactory.setPort(21);

        ftpServerFactory.addListener("default", listenerFactory.createListener());
        ftpServerFactory.setUserManager(userManagerFactory.createUserManager());

        for (BaseUser user : getUsers(path)) {
            ftpServerFactory.getUserManager().save(user);
        }

        final FtpServer ftpServer = ftpServerFactory.createServer();
        ftpServer.start();


    }


    private static List<BaseUser> getUsers(Path dir) throws IOException {
        final List<BaseUser> users = new ArrayList<>();
        for (String s : StringUtils.defaultIfBlank(System.getenv("USERS"), "admin|admin").split(",")) {
            final String[] segments = s.split("\\|");
            final String username = segments[0];
            final String password = segments[1];
            if (users.stream().anyMatch(u -> u.getName().equals(username))) {
                throw new IllegalArgumentException("Username [" + username + "] duplicates");
            }
            final BaseUser user = new BaseUser();
            user.setName(username);
            user.setPassword(password);
            final Path path = dir.resolve(username);
            if (Files.notExists(path)) {
                Files.createDirectories(path);
            }
            user.setHomeDirectory(path.toAbsolutePath().toString());
            user.setAuthorities(List.of(new WritePermission()));
            users.add(user);
        }
        return users;
    }
}
