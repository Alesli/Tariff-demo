package dao;

import entity.Tariff;
import entity.User;
import jdbc.ServerConnector;
import jdbc.ServerQuery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * класс реализует интерфейс UserDao, получает данные из таблиц БД MySQL
 * через запросы из файла mysql_queries.properties с использованием JDBC.
 *
 * @author Alesia Skarakhod
 */
public class UserDaoImpl implements UserDao {

     private Connection connection = ServerConnector.getInstance(null).getConnection();
    private Map<String, String> queries = ServerQuery.getInstance(null).getQueries();

    /**
     * переопределеный метод, получает список пользователеей
     *
     * @return результат запроса getAllUser из файла mysql_queries.properties
     */
    @Override
    public List<User> findAll() {

        List<User> userList = new ArrayList<>();
        try {
            String query = queries.get("getAllUsers");
            PreparedStatement userPreparedStatement = connection.prepareStatement(query);
            ResultSet userResultSet = userPreparedStatement.executeQuery();
            while (userResultSet.next()) {
                User user = getUser(userResultSet);
                query = queries.get("getAllTariffsByUserId");
                PreparedStatement tariffPreparedStatement = connection.prepareStatement(query);
                tariffPreparedStatement.setLong(1, user.getId());
                ResultSet tariffResultSet = tariffPreparedStatement.executeQuery();
                List<Tariff> tariffList = getTariff(tariffResultSet);
                user.setTariffList(tariffList);
                userList.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userList;
    }

    private User getUser(ResultSet resultSet) {
        try {
            return User
                    .builder()
                    .id(resultSet.getLong(1))
                    .name(resultSet.getString(2))
                    .lastName(resultSet.getString(3))
                    .build();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Tariff> getTariff(ResultSet resultSet) {
        try {
            List<Tariff> tariffList = new ArrayList<>();
            while (resultSet.next()) {
                tariffList.add(
                        Tariff
                                .builder()
                                .id(resultSet.getLong(1))
                                .userId(resultSet.getLong(2))
                                .name(resultSet.getString(3))
                                .balance(resultSet.getDouble(4))
                                .build()
                );
            }
            return tariffList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
