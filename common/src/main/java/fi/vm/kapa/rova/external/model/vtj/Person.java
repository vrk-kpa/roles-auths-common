package fi.vm.kapa.rova.external.model.vtj;

import java.util.ArrayList;
import java.util.List;

import fi.vm.kapa.rova.engine.evaluation.Evaluable;

public class Person implements Evaluable {

    private String hetu;

    private boolean hetuValid;

    private String firstNames;

    private String lastName;

    private String birthdate;

    private List<Person> principals = new ArrayList<Person>(); // huollettavat (sekä lapset että edunvalvottavat)

    private List<Person> edunvalvojat = new ArrayList<Person>(); // henkilö edunvalvojat

    private List<Person> edunvalvontaValtuutetut = new ArrayList<Person>(); //henkilöedunvalvontavaltuutetut

    private List<Person> huoltajat = new ArrayList<Person>(); // huoltajat

    private boolean deceased; // kuollut

    private boolean turvakielto; // turvakielto

    private boolean huostaanotettu;

    private boolean edunvalvonta; //edunvalvoja määrätty

    private boolean edunvalvontaRajoitettu; //edunvalvonta rajoitettu

    private boolean edunvalvontaEiRajoitettu; //edunvalvontaa ei rajoitettu

    private boolean edunvalvontaJulistettu; //edunvalvonta julistettu

    private boolean huollonjakoSopimus;

    private boolean huollonjakoMaarays;

    public String getHetu() {
        return hetu;
    }

    public void setHetu(String hetu) {
        this.hetu = hetu;
    }

    public boolean isHetuValid() {
        return hetuValid;
    }

    public void setHetuValid(boolean hetuValid) {
        this.hetuValid = hetuValid;
    }

    public String getFirstNames() {
        return firstNames;
    }

    public void setFirstNames(String firstNames) {
        this.firstNames = firstNames;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public boolean isDeceased() {
        return deceased;
    }

    public void setDeceased(boolean deceased) {
        this.deceased = deceased;
    }

    public boolean isTurvakielto() {
        return turvakielto;
    }

    public void setTurvakielto(boolean turvakielto) {
        this.turvakielto = turvakielto;
    }

    public boolean isHuostaanotettu() {
        return huostaanotettu;
    }

    public void setHuostaanotettu(boolean huostaanotettu) {
        this.huostaanotettu = huostaanotettu;
    }

    public List<Person> getEdunvalvojat() {
        return edunvalvojat;
    }

    public void setEdunvalvojat(List<Person> edunvalvojat) {
        this.edunvalvojat = edunvalvojat;
    }

    public List<Person> getEdunvalvontaValtuutetut() {
        return edunvalvontaValtuutetut;
    }

    public void setEdunvalvontaValtuutetut(
            List<Person> edunvalvontaValtuutetut) {
        this.edunvalvontaValtuutetut = edunvalvontaValtuutetut;
    }

    public List<Person> getHuoltajat() {
        return huoltajat;
    }

    public void setHuoltajat(List<Person> huoltajat) {
        this.huoltajat = huoltajat;
    }

    public List<Person> getPrincipals() {
        return principals;
    }

    public void setPrincipals(List<Person> principals) {
        this.principals = principals;
    }

    public boolean isEdunvalvonta() {
        return edunvalvonta;
    }

    public void setEdunvalvonta(boolean edunvalvonta) {
        this.edunvalvonta = edunvalvonta;
    }

    public boolean isEdunvalvontaRajoitettu() {
        return edunvalvontaRajoitettu;
    }

    public void setEdunvalvontaRajoitettu(boolean edunvalvontaRajoitettu) {
        this.edunvalvontaRajoitettu = edunvalvontaRajoitettu;
    }

    public boolean isEdunvalvontaEiRajoitettu() {
        return edunvalvontaEiRajoitettu;
    }

    public void setEdunvalvontaEiRajoitettu(boolean edunvalvontaEiRajoitettu) {
        this.edunvalvontaEiRajoitettu = edunvalvontaEiRajoitettu;
    }

    public boolean isEdunvalvontaJulistettu() {
        return edunvalvontaJulistettu;
    }

    public void setEdunvalvontaJulistettu(boolean edunvalvontaJulistettu) {
        this.edunvalvontaJulistettu = edunvalvontaJulistettu;
    }

    public boolean isHuollonjakoSopimus() {
        return huollonjakoSopimus;
    }

    public void setHuollonjakoSopimus(boolean huollonjakoSopimus) {
        this.huollonjakoSopimus = huollonjakoSopimus;
    }

    public boolean isHuollonjakoMaarays() {
        return huollonjakoMaarays;
    }

    public void setHuollonjakoMaarays(boolean huollonjakoMaarays) {
        this.huollonjakoMaarays = huollonjakoMaarays;
    }

    @Override
    public String toString() {
        return "Person [ssn=" + hetu + ", ssnValid=" + hetuValid
                + ", firstNames=" + firstNames + ", lastName=" + lastName
                + ", principals=" + principals + ", guardians=" + edunvalvojat
                + ", custodians=" + huoltajat + ", deceased=" + deceased
                + ", protectionOrder=" + turvakielto + ", huostaanotettu="
                + huostaanotettu + ", guardianship=" + edunvalvonta + ", huollonjakosopimus="
                + huollonjakoSopimus + ", huollonjakomaarays=" + huollonjakoMaarays + "]";
    }
}
