package edu.eci.cvds.samples.services.impl;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import edu.eci.cvds.sampleprj.dao.ClienteDAO;
import edu.eci.cvds.sampleprj.dao.ItemDAO;
import edu.eci.cvds.sampleprj.dao.PersistenceException;

import edu.eci.cvds.samples.entities.Cliente;
import edu.eci.cvds.samples.entities.Item;
import edu.eci.cvds.samples.entities.ItemRentado;
import edu.eci.cvds.samples.entities.TipoItem;
import edu.eci.cvds.samples.services.ExcepcionServiciosAlquiler;
import edu.eci.cvds.samples.services.ServiciosAlquiler;
import edu.eci.cvds.samples.services.ServiciosAlquilerFactory;
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import sun.applet.Main;

@Singleton
public class ServiciosAlquilerItemsImpl implements ServiciosAlquiler {

   @Inject
   private ItemDAO itemDAO;
   @Inject
   private ClienteDAO clienteDAO;

   @Override
   public int valorMultaRetrasoxDia(int itemId) {
       throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public Cliente consultarCliente(long docu) throws ExcepcionServiciosAlquiler {
       try {
           return clienteDAO.load(docu);
       } catch (PersistenceException ex) {
           throw new ExcepcionServiciosAlquiler("Error al consultar el cliente "+docu,ex);
       }
   }

   @Override
   public List<ItemRentado> consultarItemsCliente(long idcliente) throws ExcepcionServiciosAlquiler {
       try{
           return clienteDAO.loadItemsRentados(idcliente);
       }catch(PersistenceException ex){
           throw new ExcepcionServiciosAlquiler("Error al consultar items rentados "+idcliente,ex);
       }
   }

   @Override
   public List<Cliente> consultarClientes() throws ExcepcionServiciosAlquiler {
       try {
           return clienteDAO.loadClientes();
       } catch (PersistenceException ex) {
           throw new ExcepcionServiciosAlquiler("Error al consultar clientes ",ex);
       }
   }

   @Override
   public Item consultarItem(int id) throws ExcepcionServiciosAlquiler {
       try {
           return itemDAO.load(id);
       } catch (PersistenceException ex) {
           throw new ExcepcionServiciosAlquiler("Error al consultar el item "+id,ex);
       }
   }

   @Override
   public List<Item> consultarItemsDisponibles() {
       throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public long consultarMultaAlquiler(int iditem, Date fechaDevolucion) throws ExcepcionServiciosAlquiler {
	   List<Cliente> clientes = consultarClientes();
       for (Cliente cliente:clientes) {
           ArrayList<ItemRentado> rentados = cliente.getRentados();
           for (ItemRentado item :rentados) {
               if( item.getItem().getId()==iditem) {
                   LocalDate fechaFin=item.getFechafinrenta().toLocalDate();
                   LocalDate fechaEntrego=fechaDevolucion.toLocalDate();
                   long diasRetraso = ChronoUnit.DAYS.between(fechaFin, fechaEntrego);
                   if(diasRetraso<0){
                       return 0;
                   }
                   return diasRetraso*valorMultaRetrasoxDia(item.getItem().getId());
               }
           }
       }
       return 0;
   }

   @Override
   public TipoItem consultarTipoItem(int id) throws ExcepcionServiciosAlquiler {
       throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public List<TipoItem> consultarTiposItem() throws ExcepcionServiciosAlquiler {
       throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public void registrarAlquilerCliente(Date date, long docu, Item item, int numdias) throws ExcepcionServiciosAlquiler {
        try {
            clienteDAO.agregarItemRentado(docu, item.getId(), date, (Date) sumarDias(date,numdias));
        }catch(PersistenceException ex) {
            throw new ExcepcionServiciosAlquiler("Error al consultar multa",ex);
        }
   }

   @Override
   public void registrarCliente(Cliente c) throws ExcepcionServiciosAlquiler {
       try {
           clienteDAO.save(c);
       } catch (PersistenceException ex) {
           throw new ExcepcionServiciosAlquiler("Error al registrar el cliente ",ex);
       }
   }

   @Override
   public long consultarCostoAlquiler(int iditem, int numdias) throws ExcepcionServiciosAlquiler {
       throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public void actualizarTarifaItem(int id, long tarifa) throws ExcepcionServiciosAlquiler {
       throw new UnsupportedOperationException("Not supported yet.");
   }
   @Override
   public void registrarItem(Item i) throws ExcepcionServiciosAlquiler {
       throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }

   @Override
   public void vetarCliente(long docu, boolean estado) throws ExcepcionServiciosAlquiler {
       throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }
   
   public static void main(String[] args) throws ExcepcionServiciosAlquiler{
       ServiciosAlquilerFactory hola = ServiciosAlquilerFactory.getInstance();
       //hola.getServiciosAlquiler().consultarItem(10);
       System.out.println(hola.getServiciosAlquiler().consultarClientes());
   }
   
   private static java.util.Date sumarDias(Date date,int numdias){
       	Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_YEAR,numdias);
        return c.getTime();
   }
}