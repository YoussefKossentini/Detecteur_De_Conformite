package core;
import java.util.List;
import model.Alerte;

public interface Exporteur {
    void exporter(List<Alerte> alertes, String cheminSortie);
}