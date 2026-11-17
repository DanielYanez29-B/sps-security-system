package com.sps.security.dao.impl;

import com.sps.security.dao.NotificacionDAO;
import com.sps.security.model.telemetry.Notificacion;
import com.sps.security.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NotificacionDAOImpl implements NotificacionDAO {
    @Override public boolean guardar(Notificacion n) {
        String sql = "INSERT INTO NOTIFICACION (id_alarma,id_usuario,fecha_hora,mensaje,leido) VALUES (?,?,?,?,?)";
        try (Connection c=DatabaseConnection.getConnection(); PreparedStatement s=c.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
            bind(s,n); if(s.executeUpdate()==0)return false; try(ResultSet k=s.getGeneratedKeys()){if(k.next())n.setIdNotificacion(k.getInt(1));} return true;
        } catch(SQLException e){throw new RuntimeException("No se pudo guardar la notificación.",e);}
    }
    @Override public Optional<Notificacion> obtenerPorId(Integer id){return consultar("SELECT * FROM NOTIFICACION WHERE id_notificacion=?",s->s.setInt(1,id)).stream().findFirst();}
    @Override public List<Notificacion> obtenerTodos(){return consultar("SELECT * FROM NOTIFICACION ORDER BY fecha_hora DESC",s->{});}
    @Override public List<Notificacion> obtenerPorAlarma(Integer id){return consultar("SELECT * FROM NOTIFICACION WHERE id_alarma=? ORDER BY fecha_hora DESC",s->s.setInt(1,id));}
    @Override public List<Notificacion> obtenerPorUsuario(Integer id){return consultar("SELECT * FROM NOTIFICACION WHERE id_usuario=? ORDER BY fecha_hora DESC",s->s.setInt(1,id));}
    @Override public List<Notificacion> obtenerNoLeidasPorUsuario(Integer id){return consultar("SELECT * FROM NOTIFICACION WHERE id_usuario=? AND leido=FALSE ORDER BY fecha_hora DESC",s->s.setInt(1,id));}
    @Override public boolean marcarComoLeida(Integer id){return ejecutar("UPDATE NOTIFICACION SET leido=TRUE WHERE id_notificacion=?",id)>0;}
    @Override public int marcarTodasComoLeidas(Integer id){return ejecutar("UPDATE NOTIFICACION SET leido=TRUE WHERE id_usuario=? AND leido=FALSE",id);}
    @Override public boolean actualizar(Notificacion n){String sql="UPDATE NOTIFICACION SET id_alarma=?,id_usuario=?,fecha_hora=?,mensaje=?,leido=? WHERE id_notificacion=?";try(Connection c=DatabaseConnection.getConnection();PreparedStatement s=c.prepareStatement(sql)){bind(s,n);s.setInt(6,n.getIdNotificacion());return s.executeUpdate()>0;}catch(SQLException e){throw new RuntimeException("No se pudo actualizar la notificación.",e);}}
    @Override public boolean eliminar(Notificacion n){if(n==null||n.getIdNotificacion()==null)return false;return ejecutar("DELETE FROM NOTIFICACION WHERE id_notificacion=?",n.getIdNotificacion())>0;}
    private void bind(PreparedStatement s,Notificacion n)throws SQLException{s.setInt(1,n.getIdAlarma());s.setInt(2,n.getIdUsuario());s.setTimestamp(3,Timestamp.valueOf(n.getFechaHora()));s.setString(4,n.getMensaje());s.setBoolean(5,Boolean.TRUE.equals(n.getLeido()));}
    private int ejecutar(String sql,Integer id){try(Connection c=DatabaseConnection.getConnection();PreparedStatement s=c.prepareStatement(sql)){s.setInt(1,id);return s.executeUpdate();}catch(SQLException e){throw new RuntimeException("No se pudo actualizar la notificación.",e);}}
    private List<Notificacion> consultar(String sql,SQLBinder b){List<Notificacion> r=new ArrayList<>();try(Connection c=DatabaseConnection.getConnection();PreparedStatement s=c.prepareStatement(sql)){b.bind(s);try(ResultSet rs=s.executeQuery()){while(rs.next())r.add(new Notificacion(rs.getInt("id_notificacion"),rs.getInt("id_alarma"),rs.getInt("id_usuario"),rs.getTimestamp("fecha_hora").toLocalDateTime(),rs.getString("mensaje"),rs.getBoolean("leido")));}return r;}catch(SQLException e){throw new RuntimeException("No se pudieron consultar las notificaciones.",e);}}
    @FunctionalInterface private interface SQLBinder{void bind(PreparedStatement s)throws SQLException;}
}
