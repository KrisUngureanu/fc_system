
package kz.tamur.or3.mtszn.natperson;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the kz.tamur.or3.mtszn.natperson package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: kz.tamur.or3.mtszn.natperson
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Person }
     * 
     */
    public Person createPerson() {
        return new Person();
    }

    /**
     * Create an instance of {@link Parent }
     * 
     */
    public Parent createParent() {
        return new Parent();
    }

    /**
     * Create an instance of {@link PersonCapableStatus }
     * 
     */
    public PersonCapableStatus createPersonCapableStatus() {
        return new PersonCapableStatus();
    }

    /**
     * Create an instance of {@link ForeignData }
     * 
     */
    public ForeignData createForeignData() {
        return new ForeignData();
    }

    /**
     * Create an instance of {@link PersonExcludeStatus }
     * 
     */
    public PersonExcludeStatus createPersonExcludeStatus() {
        return new PersonExcludeStatus();
    }

    /**
     * Create an instance of {@link Fio }
     * 
     */
    public Fio createFio() {
        return new Fio();
    }

    /**
     * Create an instance of {@link MissingStatus }
     * 
     */
    public MissingStatus createMissingStatus() {
        return new MissingStatus();
    }

    /**
     * Create an instance of {@link AddDocs }
     * 
     */
    public AddDocs createAddDocs() {
        return new AddDocs();
    }

    /**
     * Create an instance of {@link Child }
     * 
     */
    public Child createChild() {
        return new Child();
    }

    /**
     * Create an instance of {@link RegAddress }
     * 
     */
    public RegAddress createRegAddress() {
        return new RegAddress();
    }

    /**
     * Create an instance of {@link DisappearStatus }
     * 
     */
    public DisappearStatus createDisappearStatus() {
        return new DisappearStatus();
    }

    /**
     * Create an instance of {@link BirthPlace }
     * 
     */
    public BirthPlace createBirthPlace() {
        return new BirthPlace();
    }

    /**
     * Create an instance of {@link AbsentStatus }
     * 
     */
    public AbsentStatus createAbsentStatus() {
        return new AbsentStatus();
    }

    /**
     * Create an instance of {@link Person.FrontierCrossings }
     * 
     */
    public Person.FrontierCrossings createPersonFrontierCrossings() {
        return new Person.FrontierCrossings();
    }

    /**
     * Create an instance of {@link Person.Documents }
     * 
     */
    public Person.Documents createPersonDocuments() {
        return new Person.Documents();
    }

}
