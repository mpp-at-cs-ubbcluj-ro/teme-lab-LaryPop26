package org.mpp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CarsDBRepository implements CarRepository {

    private JdbcUtils dbUtils;

    private static final Logger logger= LogManager.getLogger();

    public CarsDBRepository(Properties props) {
        logger.info("Initializing CarsDBRepository with properties: {} ",props);
        dbUtils=new JdbcUtils(props);
    }

    @Override
    public List<Car> findByManufacturer(String manufacturerN) {
        List<Car> cars = new ArrayList<>();
        logger.traceEntry("Finding cars by manufacturer: {}", manufacturerN);
        Connection con = dbUtils.getConnection();

        try (PreparedStatement preStm = con.prepareStatement("SELECT * FROM cars WHERE manufacturer = ?")) {
            preStm.setString(1, manufacturerN);

            try (ResultSet resultSet = preStm.executeQuery()) {
                while (resultSet.next()) {
                    String manufacturer = resultSet.getString("manufacturer");
                    String model = resultSet.getString("model");
                    int year = resultSet.getInt("year");

                    Car car = new Car(manufacturer, model, year);
                    cars.add(car);
                }
            }

        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error " + e);
        }

        logger.traceExit("Found {} cars", cars.size());
        return cars;
    }

    @Override
    public List<Car> findBetweenYears(int min, int max) {
        List<Car> cars = new ArrayList<>();
        logger.traceEntry("Finding cars between years: {} - {}", min, max);
        Connection con = dbUtils.getConnection();

        try (PreparedStatement preStm = con.prepareStatement("SELECT * FROM cars WHERE year BETWEEN ? AND ?")) {
            preStm.setInt(1, min);
            preStm.setInt(2, max);

            try (ResultSet resultSet = preStm.executeQuery()) {
                while (resultSet.next()) {
                    String manufacturer = resultSet.getString("manufacturer");
                    String model = resultSet.getString("model");
                    int year = resultSet.getInt("year");

                    Car car = new Car(manufacturer, model, year);
                    cars.add(car);
                }
            }

        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error " + e);
        }

        logger.traceExit("Found {} cars", cars.size());
        return cars;
    }

    @Override
    public void add(Car elem) {
        logger.traceEntry("saving task {}", elem);
        Connection con=dbUtils.getConnection();
        try(PreparedStatement preStm = con.prepareStatement("insert into cars(manufacturer, model, year) values (?,?,?)")) {
            preStm.setString(1, elem.getManufacturer());
            preStm.setString(2, elem.getModel());
            preStm.setInt(3, elem.getYear());
            int result = preStm.executeUpdate();
            logger.trace("Saved {} instances", result);
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error " + e);
        }
        logger.traceExit();
    }

    @Override
    public void update(Integer integer, Car elem) {
        logger.traceEntry("saving task {}", elem);
        Connection con=dbUtils.getConnection();
        try(PreparedStatement preStm = con.prepareStatement("update cars SET manufacturer=?, model=?, year=? WHERE id=?")) {
            preStm.setString(1, elem.getManufacturer());
            preStm.setString(2, elem.getModel());
            preStm.setInt(3, elem.getYear());
            preStm.setInt(4, integer);

            int result = preStm.executeUpdate();
            logger.trace("Saved {} instances", result);
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error " + e);
        }
        logger.traceExit();
    }

    @Override
    public Iterable<Car> findAll() {
        logger.traceEntry();
        Connection con=dbUtils.getConnection();
        List<Car> cars=new ArrayList<>();
        try(PreparedStatement preStm=con.prepareStatement("select * from cars")) {
            try(ResultSet result=preStm.executeQuery()) {
                while (result.next()) {
                    int id = result.getInt("id");
                    String manufacturer = result.getString("manufacturer");
                    String model = result.getString("model");
                    int year = result.getInt("year");
                    Car car = new Car(manufacturer, model, year);
                    car.setId(id);
                    cars.add(car);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error " + e);
        }
        logger.traceExit(cars);
	    return cars;
    }
}
