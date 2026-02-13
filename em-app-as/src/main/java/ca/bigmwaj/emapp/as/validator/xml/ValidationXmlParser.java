package ca.bigmwaj.emapp.as.validator.xml;

import ca.bigmwaj.emapp.as.validator.xml.model.*;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

/**
 * Parses XML validation configuration files.
 */
@Component
public class ValidationXmlParser {

    /**
     * Parses an XML validation configuration file.
     * 
     * @param inputStream The XML file input stream
     * @return ValidationConfig object
     * @throws ValidationConfigurationException if parsing fails
     */
    public ValidationConfig parse(InputStream inputStream) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);
            
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            ValidationConfig config = new ValidationConfig();
            
            Element root = document.getDocumentElement();
            if (!"validation".equals(root.getNodeName())) {
                throw new ValidationConfigurationException("Root element must be 'validation'");
            }

            NodeList entryNodes = root.getElementsByTagName("entry");
            for (int i = 0; i < entryNodes.getLength(); i++) {
                Node entryNode = entryNodes.item(i);
                if (entryNode.getNodeType() == Node.ELEMENT_NODE) {
                    ValidationEntry entry = parseEntry((Element) entryNode);
                    config.getEntries().add(entry);
                }
            }

            return config;
        } catch (Exception e) {
            throw new ValidationConfigurationException("Failed to parse validation XML", e);
        }
    }

    private ValidationEntry parseEntry(Element entryElement) {
        ValidationEntry entry = new ValidationEntry();
        entry.setName(entryElement.getAttribute("name"));

        NodeList fieldNodes = entryElement.getElementsByTagName("field");
        for (int i = 0; i < fieldNodes.getLength(); i++) {
            Node fieldNode = fieldNodes.item(i);
            if (fieldNode.getNodeType() == Node.ELEMENT_NODE && fieldNode.getParentNode().equals(entryElement)) {
                FieldValidation field = parseField((Element) fieldNode);
                entry.getFields().add(field);
            }
        }

        return entry;
    }

    private FieldValidation parseField(Element fieldElement) {
        FieldValidation field = new FieldValidation();
        field.setName(fieldElement.getAttribute("name"));

        NodeList conditionNodes = fieldElement.getElementsByTagName("condition");
        for (int i = 0; i < conditionNodes.getLength(); i++) {
            Node conditionNode = conditionNodes.item(i);
            if (conditionNode.getNodeType() == Node.ELEMENT_NODE && conditionNode.getParentNode().equals(fieldElement)) {
                ConditionConfig condition = parseCondition((Element) conditionNode);
                field.getConditions().add(condition);
            }
        }

        return field;
    }

    private ConditionConfig parseCondition(Element conditionElement) {
        ConditionConfig condition = new ConditionConfig();
        condition.setExpression(conditionElement.getAttribute("expression"));

        NodeList ruleNodes = conditionElement.getElementsByTagName("rule");
        for (int i = 0; i < ruleNodes.getLength(); i++) {
            Node ruleNode = ruleNodes.item(i);
            if (ruleNode.getNodeType() == Node.ELEMENT_NODE && ruleNode.getParentNode().equals(conditionElement)) {
                RuleConfig rule = parseRule((Element) ruleNode);
                condition.getRules().add(rule);
            }
        }

        return condition;
    }

    private RuleConfig parseRule(Element ruleElement) {
        RuleConfig rule = new RuleConfig();
        rule.setType(ruleElement.getAttribute("type"));

        // Parse all attributes as parameters (except 'type')
        for (int i = 0; i < ruleElement.getAttributes().getLength(); i++) {
            Node attr = ruleElement.getAttributes().item(i);
            String attrName = attr.getNodeName();
            if (!"type".equals(attrName)) {
                rule.getParameters().put(attrName, attr.getNodeValue());
            }
        }

        return rule;
    }
}
