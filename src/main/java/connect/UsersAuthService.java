package connect;

import server.AuthService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsersAuthService implements AuthService {

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        return SpaceRepository.authServ(login, password);
    }

    @Override
    public boolean registration(String login, String password, String nickname) {
        return SpaceRepository.createUsers(login, password, nickname);
    }

    @Override
    public boolean changeNickname(String trueName, String newNick) {
        return SpaceRepository.changeNickname(trueName, newNick);
    }
}
