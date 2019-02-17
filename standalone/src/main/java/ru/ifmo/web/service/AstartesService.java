package ru.ifmo.web.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.ifmo.web.database.dao.AstartesDAO;
import ru.ifmo.web.database.entity.Astartes;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@WebService(serviceName = "astartes", targetNamespace = "astartes_namespace")
@AllArgsConstructor
@NoArgsConstructor
public class AstartesService {
    private AstartesDAO astartesDAO;

    @WebMethod
    public List<Astartes> findAll() throws SQLException {
        return astartesDAO.findAll();
    }

    @WebMethod
    public List<Astartes> findWithFilters(@WebParam(name = "id") Long id, @WebParam(name = "name") String name,
                                          @WebParam(name = "title") String title, @WebParam(name = "position") String position,
                                          @WebParam(name = "planet") String planet, @WebParam(name = "birthdate") Date birthdate) throws SQLException {
        return astartesDAO.findWithFilters(id, name, title, position, planet, birthdate);
    }

    @WebMethod
    public int update(@WebParam(name = "id") Long id, @WebParam(name = "name") String name,
                      @WebParam(name = "title") String title, @WebParam(name = "position") String position,
                      @WebParam(name = "planet") String planet, @WebParam(name = "birthdate") Date birthdate) throws SQLException {
        return astartesDAO.update(id, name, title, position, planet, birthdate);
    }

    @WebMethod
    public int delete(@WebParam(name = "id") Long id) throws SQLException {
        return astartesDAO.delete(id);
    }

    @WebMethod
    public Long create(@WebParam(name = "name") String name,
                       @WebParam(name = "title") String title, @WebParam(name = "position") String position,
                       @WebParam(name = "planet") String planet, @WebParam(name = "birthdate") Date birthdate) throws SQLException {
        return astartesDAO.create(name, title, position, planet, birthdate);
    }
}
