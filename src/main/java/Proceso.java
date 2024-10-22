
class Proceso {
    String id; // Ahora el ID será una letra
    int tiempoLlegada;
    int rafaga;
    int tiempoRestante;
    int tiempoFinal;
    int tiempoEspera;
    int tiempoRetorno;

    public Proceso(String id, int tiempoLlegada, int rafaga) {
        this.id = id;
        this.tiempoLlegada = tiempoLlegada;
        this.rafaga = rafaga;
        this.tiempoRestante = rafaga;
    }
}