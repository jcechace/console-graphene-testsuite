package org.jboss.as.console.testsuite.util;

import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.findby.ByJQuery;
import org.jboss.as.console.testsuite.fragments.BaseFragment;
import org.jboss.as.console.testsuite.fragments.RadioButton;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by jcechace on 03/03/14.
 */
public class Editor extends BaseFragment {

    private static final Logger log = LoggerFactory.getLogger(Editor.class);
    
    /**
     * Returns either a input of type text or textarea element with given identifier (name or id).
     *
     * @param identifier
     * @return a text element
     */
    public WebElement getText(String identifier) {
        return findTextElement(identifier);
    }

    /**
     * Sets the value of given text element.
     *
     * @param identifier
     * @param value
     */
    public void text(String identifier, String value) {
        WebElement input = getText(identifier);
        input.clear();
        log.debug("setting value '{}' to the text element '{}'", value, identifier);
        Graphene.waitGui().until().element(input).value().equalTo("");
        input.sendKeys(value);
        Graphene.waitGui().until().element(input).value().equalTo(value);
    }

    /**
     * Reads the value from text element with given identifier.
     *
     * @param identifier
     * @return a value of text element
     */
    public String text(String identifier) {
        WebElement input = getText(identifier);

        return input.getAttribute("value");
    }

    /**
     * Returns a password input with given identifier
     *
     * @param identifier
     * @return a password input
     */
    public WebElement getPassword(String identifier) {
        return findInputElement("password", identifier);
    }

    public void password(String identifier, String value) {
        WebElement input = getPassword(identifier);
        input.clear();
        Graphene.waitGui().until().element(input).value().equalTo("");
        input.sendKeys(value);
        log.debug("password '{}' was set", input.getText());
    }

    /**
     * Returns file input element with a given identifier
     *
     * @param identifier
     * @return a file input element
     */
    public WebElement getFileInputElement(String identifier) {
        return findInputElement("file", identifier);
    }

    /**
     *
     * @param fileToUpload
     * @param identifier
     */
    public void uploadFile(File fileToUpload, String identifier) {
        WebElement fileInput = getFileInputElement(identifier);
        log.debug("uploading file '{}'", fileToUpload.toString());
        fileInput.sendKeys(fileToUpload.getAbsolutePath());
        Graphene.waitGui().until().element(fileInput).value().equalTo(fileToUpload.getAbsolutePath());
    }


    /**
     * Returns a checkbox with given identifier.
     * @param identifier
     * @return a checkbox element
     */
    public WebElement getCheckbox(String identifier) {
        return findInputElement("checkbox", identifier);
    }

    /**
     * Sets the value of checkbox with given identifier.
     *
     * @param identifier
     * @param value
     */
    public void checkbox(String identifier, boolean value) {
        WebElement input = getCheckbox(identifier);
        boolean current = input.isSelected();

        log.debug("{} checkbox '{}'", (value ? "setting" : "unsetting"), identifier);
        if (value != current) {
            input.click();
        }

        if (value) {
            Graphene.waitGui().until().element(input).is().selected();
        } else {
            Graphene.waitGui().until().element(input).is().not().selected();
        }
    }

    /**
     * Reads  the value of checkbox with given identifier.
     *
     * @param identifier
     * @return value of checkbox
     */
    public boolean  checkbox(String identifier) {
        WebElement input = getCheckbox(identifier);

        boolean res = input.isSelected();
        log.debug("checkbox '{}' {} set", identifier, (res ? "is" : "isn't"));

        return res;
    }


    /**
     *  Returns a property editor object found within editor's root.
     *
     * @return a property editor
     */
    public PropertyEditor properties() {
        PropertyEditor properties = Console.withBrowser(browser).getPropertyEditor(root);

        return properties;
    }

    private WebElement findTextElement(String identifier) {
        WebElement text = null;
        try {
            text = findInputElement("text", identifier);
        } catch (NoSuchElementException ignore) {
            log.debug("not found - looking for textarea '{}'", identifier);

            String byIdSelector = "textarea[id$='" + identifier + "'], ";
            String byNameSelector = "textarea[name='" + identifier + "'], ";
            By selector = ByJQuery.selector(byIdSelector + ", " + byNameSelector);

            text = findElement(selector, root);
        }

        return text;
    }

    private WebElement findInputElement(String type, String identifier) {
        log.trace("looking for the '{}' input element identified by '{}'", type, identifier);

        String byIdSelector = "input[type='" + type + "'][id$='" + identifier + "'], ";
        String byNameSelector = "input[type='" + type + "'][name='" + identifier + "'], ";
        By selector = ByJQuery.selector(byIdSelector + ", " + byNameSelector);

        return findElement(selector, root);
    }

    private WebElement findElement(By selector, WebElement root) {
        return Console.withBrowser(browser).findElement(selector, root);
    }

    /**
     * @param name name of the radio button input elements
     * @return radio button related to the presented name
     */
    private RadioButton findRadioButton(String name) {
        log.debug("looking for the radio buttons for '{}'", name);
        RadioButton button = Graphene.createPageFragment(RadioButton.class, root);
        button.findChoices(name);
        return button;
    }

    /**
     * Select the index-th radio button of given name
     * @param name name of the radio button input elements
     * @param index index of the radio button to select
     */
    public void radioButton(String name, int index) {
        RadioButton button = findRadioButton(name);
        log.debug("picking {}-th radio button", index);
        button.pick(index);
        Graphene.waitGui().until().element(button.getInputElement(index)).is().selected();
    }

    /**
     * @param name name of the radio button input elements
     * @return the index of selected radio button of given name
     */
    public int radioButton(String name) {
        RadioButton button = findRadioButton(name);

        int index = button.getIndex();
        log.debug("{}-th radio button is selected", index);

        return index;
    }
}
