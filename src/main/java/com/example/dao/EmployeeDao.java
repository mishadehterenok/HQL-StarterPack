package com.example.dao;

import com.example.entity.Employee;
import com.example.entity.Payment;
import org.hibernate.Session;

import java.util.List;

public final class EmployeeDao {

    private static EmployeeDao INSTANCE;

    private EmployeeDao() {
    }

    public static EmployeeDao getInstance() {
        if (INSTANCE == null) {
            synchronized (EmployeeDao.class) {
                if (INSTANCE == null) {
                    INSTANCE = new EmployeeDao();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Возвращает всех сотрудников
     */
    public List<Employee> findAll(Session session) {
        return session
                .createQuery("select e from Employee e", Employee.class)
                .getResultList();
    }

    /**
     * Возвращает всех сотрудников с указанным именем
     */
    public List<Employee> findAllByFirstName(Session session, String firstName) {
        return session
                .createQuery("select e from Employee e where e.firstName=:firstName", Employee.class)
                .setParameter("firstName", firstName)
                .getResultList();
    }

    /**
     * Возвращает первые {limit} сотрудников, упорядоченных по дате рождения (в порядке возрастания)
     */
    public List<Employee> findLimitedEmployeesOrderedByBirthday(Session session, int limit) {
        return session
                .createQuery("select e from Employee e order by e.birthday asc", Employee.class)
                .setMaxResults(limit)
                .getResultList();
    }

    /**
     * Возвращает всех сотрудников организации с указанным названием
     */
    public List<Employee> findAllByOrganizationName(Session session, String organizationName) {
        return session
                .createQuery("select e from Employee e where e.organization.name=:orgName", Employee.class)
                .setParameter("orgName", organizationName)
                .getResultList();
    }

    /**
     * Возвращает все выплаты, полученные сотрудниками организации с указанными именем,
     * упорядоченные по имени сотрудника, а затем по размеру выплаты
     */
    public List<Payment> findAllPaymentsByOrganizationName(Session session, String organizationName) {
        return session
                .createQuery("select p from Payment p where p.receiver.organization.name=:orgName " +
                        "order by p.receiver.firstName, p.amount", Payment.class)
                .setParameter("orgName", organizationName)
                .getResultList();
    }

    /**
     * Возвращает среднюю зарплату сотрудника с указанными именем и фамилией
     */
    public Double findAveragePaymentAmountByFirstAndLastNames(Session session, String firstName, String lastName) {
        return session.createQuery("select avg(p.amount) from Payment p where p.receiver.firstName=:first and p.receiver.lastName=:second", Double.class)
                .setParameter("first", firstName)
                .setParameter("second", lastName)
                .getSingleResult();
    }

    /**
     * Возвращает для каждой организации: название, среднюю зарплату всех её сотрудников.
     * Организации упорядочены по названию.
     */
    public List<Object[]> findOrganizationNamesWithAvgEmployeePaymentsOrderedByOrgName(Session session) {
        return session.createQuery("select p.receiver.organization.name, avg(p.amount) from Payment p " +
                        "group by p.receiver.organization order by p.receiver.organization.name", Object[].class)
                .getResultList();
    }

    public List<Object[]> ffindOrganizationNamesWithAvgEmployeePaymentsOrderedByOrgName(Session session) {
        return session.createQuery("select o.name, avg(p.amount) from Payment p " +
                        "join  p.receiver.organization o group by o order by o.name", Object[].class)
                .getResultList();
    }

    /**
     * Возвращает список: сотрудник (объект Employee), средний размер выплат, но только для тех сотрудников,
     * чей средний размер выплат
     * больше среднего размера выплат всех сотрудников
     * Упорядочить по имени сотрудника
     */
    public List<Object[]> canYouDoIt(Session session) {
        return session.createQuery("select p.receiver, avg(p.amount) from Payment p " +
                        "group by p.receiver having avg (p.amount) > " +
                        "(select avg(p2.amount) from Payment p2 " +
                        "where p2.receiver.organization.name = p.receiver.organization.name)" +
                        "order by p.receiver.firstName", Object[].class)
                .getResultList();
    }
}
