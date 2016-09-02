/**
 * The MIT License
 * Copyright (c) 2016 Population Register Centre
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package fi.vm.kapa.rova.external.model.vtj;

import fi.vm.kapa.rova.engine.evaluation.Evaluable;

import java.util.ArrayList;
import java.util.List;

public class Person implements Evaluable {

    private String hetu;

    private boolean hetuValid;

    private String firstNames;

    private String lastName;
    
    private String callingName;

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
    
    public String getCallingName() {
        return callingName;
    }
    
    public void setCallingName(String callingName) {
        this.callingName = callingName;
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
