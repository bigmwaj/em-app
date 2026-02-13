package ca.bigmwaj.emapp.as.validator.xml;

import ca.bigmwaj.emapp.as.validator.xml.model.ConditionConfig;
import ca.bigmwaj.emapp.as.validator.xml.model.FieldValidation;
import ca.bigmwaj.emapp.as.validator.xml.model.RuleConfig;
import ca.bigmwaj.emapp.as.validator.xml.model.ValidationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static final Logger logger = LoggerFactory.getLogger(ValidationXmlParser.class);

    @Autowired
    private ValidationNamespaceResolver namespaceResolver;

    public ValidationConfig getValidationConfig(String namespace) {
        try {
            logger.debug("Resolve XML stream for namespace: {}", namespace);
            var xmlStream = namespaceResolver.resolveNamespace(namespace);
            return parse(xmlStream);
        } catch (Exception e) {
            logger.error("Error loading validation configuration for namespace: {}", namespace, e);
            throw new ValidationConfigurationException("Failed to load validation configuration", e);
        }
    }

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

            var root = document.getDocumentElement();
            if (!"validation".equals(root.getNodeName())) {
                throw new ValidationConfigurationException("Root element must be 'validation'");
            }

            return parseValidator(root);
        } catch (Exception e) {
            throw new ValidationConfigurationException("Failed to parse validation XML", e);
        }
    }

    private ValidationConfig parseValidator(Element configElement) {
        var config = new ValidationConfig();

        var fieldNodes = configElement.getElementsByTagName("field");
        for (int i = 0; i < fieldNodes.getLength(); i++) {
            Node fieldNode = fieldNodes.item(i);
            if (fieldNode.getNodeType() == Node.ELEMENT_NODE && fieldNode.getParentNode().equals(configElement)) {
                FieldValidation field = parseField((Element) fieldNode);
                config.getFields().add(field);
            }
        }

        return config;
    }

    private FieldValidation parseField(Element fieldElement) {
        FieldValidation field = new FieldValidation();
        field.setName(fieldElement.getAttribute("name"));
        var typeAttr = fieldElement.getAttribute("type");

        if (!typeAttr.isEmpty()) {
            try {
                field.setType(FieldValidation.fieldType.valueOf(typeAttr));
            } catch (IllegalArgumentException e) {
                throw new ValidationConfigurationException("Invalid field type: " + typeAttr, e);
            }
        } else {
            field.setType(FieldValidation.fieldType.field); // Default type
        }

        if (field.getType() == FieldValidation.fieldType.dto || field.getType() == FieldValidation.fieldType.dtos) {
            NodeList validationNodes = fieldElement.getElementsByTagName("validationConfig");
            if (validationNodes.getLength() > 0) {
                Node validationNode = validationNodes.item(0);

                if (validationNode.getNodeType() == Node.ELEMENT_NODE && validationNode.getParentNode().equals(fieldElement)) {
                    var ref = ((Element)validationNode).getAttribute("ref");
                    if (!ref.isEmpty()) {
                        var refConfig = getValidationConfig(ref);
                        field.setValidationConfig(refConfig);
                    } else {
                        var config = parseValidator((Element) validationNode);
                        field.setValidationConfig(config);
                    }
                }
            }
        }

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
