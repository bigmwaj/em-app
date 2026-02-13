# XML-Driven DTO Validation

## Overview

This implementation provides declarative DTO validation based on Spring XML configuration files. The system extends the existing validation infrastructure to support XML-driven validation rules without breaking existing validation logic.

## Architecture

### Components

1. **`@ValidDto` Annotation** - Marks DTOs that require XML-based validation
2. **`SpringDtoValidator`** - Main validator that processes XML configurations
3. **`ValidationNamespaceResolver`** - Resolves namespace to XML file location
4. **`ValidationXmlParser`** - Parses XML validation configuration
5. **`RuleFactory`** - Creates rule instances from XML configuration
6. **`ConditionEvaluator`** - Evaluates condition expressions from XML
7. **Existing Rule Classes** - `NonNullRule`, `NonEmptyRule`, `MaxLengthRule`

### Flow

```
DTO with @ValidDto annotation
    ↓
Spring triggers SpringDtoValidator
    ↓
Resolve namespace to XML file
    ↓
Parse XML configuration
    ↓
Build rule chain for each field
    ↓
Execute validation rules
    ↓
Add violations to BindingResult
```

## Usage

### 1. Annotate Your DTO

```java
@ValidDto("platform/account")
@Data
public class AccountDto extends BaseHistDto {
    private Long id;
    private String name;
    private String description;
    private AccountStatusLvo status;
    private EditActionLvo editAction;
}
```

### 2. Create XML Validation Configuration

File: `src/main/resources/validator/platform.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<validation>
    <entry name="account">
        <!-- ID validation: required for UPDATE -->
        <field name="id">
            <condition expression="editAction == 'UPDATE'">
                <rule type="NonNullRule"/>
            </condition>
        </field>

        <!-- Name validation: required for CREATE and UPDATE -->
        <field name="name">
            <condition expression="editAction == 'CREATE'">
                <rule type="NonNullRule"/>
                <rule type="MaxLengthRule" maxLength="32"/>
            </condition>
            <condition expression="editAction == 'UPDATE'">
                <rule type="NonNullRule"/>
                <rule type="MaxLengthRule" maxLength="32"/>
            </condition>
        </field>

        <!-- Description validation: optional but with max length -->
        <field name="description">
            <condition expression="true">
                <rule type="MaxLengthRule" maxLength="100"/>
            </condition>
        </field>

        <!-- Status validation: always required -->
        <field name="status">
            <condition expression="true">
                <rule type="NonNullRule"/>
            </condition>
        </field>
    </entry>
</validation>
```

### 3. Use in Controller

```java
@RestController
@RequestMapping("/api/v1/platform/account")
public class AccountController {
    
    @PostMapping
    public ResponseEntity<ResponseMessage<AccountDto>> create(
            @RequestBody @Valid AccountDto dto) {
        return ResponseEntity.ok(new ResponseMessage<>(service.create(dto)));
    }
}
```

## Namespace Resolution

The namespace in `@ValidDto` annotation follows this pattern:

- **Format**: `<directory>/<entry>`
- **Example**: `"platform/account"`
  - Directory: `platform` → File: `validator/platform.xml`
  - Entry: `account` → XML entry with `name="account"`

Alternative format:
- **Format**: `<directory>.<entry>`
- **Example**: `"account.create"`
  - Directory: `account` → File: `validator/account.xml`
  - Entry: `create` → XML entry with `name="create"`

## XML Structure

### Root Element
```xml
<validation>
    <!-- entries -->
</validation>
```

### Entry Element
Defines a validation entry point.
```xml
<entry name="account">
    <!-- field validations -->
</entry>
```

### Field Element
Defines validation for a specific field.
```xml
<field name="fieldName">
    <!-- conditions -->
</field>
```

### Condition Element
Defines when rules should be applied.
```xml
<condition expression="editAction == 'CREATE'">
    <!-- rules -->
</condition>
```

Supported expressions:
- `true` - Always apply rules
- `false` - Never apply rules
- `fieldName == 'VALUE'` - Equality check
- `fieldName != 'VALUE'` - Inequality check

### Rule Element
Defines a validation rule.

**NonNullRule**: Checks if value is not null
```xml
<rule type="NonNullRule"/>
```

**NonEmptyRule**: Checks if collection is not empty
```xml
<rule type="NonEmptyRule"/>
```

**MaxLengthRule**: Checks if string length is within limit
```xml
<rule type="MaxLengthRule" maxLength="32"/>
```

## Available Rules

| Rule Type | Description | Parameters |
|-----------|-------------|------------|
| `NonNullRule` | Value must not be null | None |
| `NonEmptyRule` | Collection must not be empty | None |
| `MaxLengthRule` | String length must not exceed limit | `maxLength` (integer) |

## Adding New Rules

To add a new validation rule:

1. Create a class extending `AbstractRule`:

```java
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class MinLengthRule extends AbstractRule {
    private int minLength;

    @Override
    public boolean isValid(Object value) {
        if (value == null) {
            return true; // Let @NotNull handle this
        }
        if (value instanceof String) {
            return ((String) value).length() >= minLength;
        }
        return false;
    }
}
```

2. Register in `RuleFactory`:

```java
public AbstractRule createRule(RuleConfig ruleConfig) {
    String ruleType = ruleConfig.getType();
    
    return switch (ruleType) {
        case "NonNullRule" -> new NonNullRule();
        case "NonEmptyRule" -> new NonEmptyRule();
        case "MaxLengthRule" -> createMaxLengthRule(ruleConfig);
        case "MinLengthRule" -> createMinLengthRule(ruleConfig); // Add this
        default -> throw new ValidationConfigurationException("Unknown rule type: " + ruleType);
    };
}

private MinLengthRule createMinLengthRule(RuleConfig ruleConfig) {
    String minLengthStr = ruleConfig.getParameters().get("minLength");
    if (minLengthStr == null || minLengthStr.isEmpty()) {
        throw new ValidationConfigurationException("MinLengthRule requires 'minLength' parameter");
    }
    try {
        int minLength = Integer.parseInt(minLengthStr);
        return new MinLengthRule(minLength);
    } catch (NumberFormatException e) {
        throw new ValidationConfigurationException(
            "Invalid minLength value for MinLengthRule: " + minLengthStr, e
        );
    }
}
```

3. Use in XML:

```xml
<rule type="MinLengthRule" minLength="5"/>
```

## Error Handling

### Configuration Errors
If XML configuration is invalid or missing, the validator:
- Logs the error
- Returns `true` (validation passes)
- Prevents application from crashing

### Validation Errors
When validation fails:
- Violations are added to `ConstraintValidatorContext`
- Field name is associated with the violation
- Spring MVC returns 400 Bad Request with error details

## Thread Safety

All components are thread-safe:
- `SpringDtoValidator` is stateless after initialization
- XML parsing is done per-request
- Rule instances are created fresh for each validation

## Integration with Existing Validation

This XML-driven validation works alongside:
- Standard Bean Validation annotations (`@NotNull`, `@Size`, etc.)
- Custom validation annotations (`@UniqueUsername`, `@ValidAccount`, etc.)
- Manual validation in services

## Performance Considerations

- XML files are parsed on each validation (consider caching for high-traffic scenarios)
- Namespace resolution uses Spring's `ClassPathResource` (efficient)
- Rule creation is lightweight
- Condition evaluation is optimized for simple expressions

## Testing

### Unit Tests
- `ValidationNamespaceResolverTest` - Tests namespace to file resolution
- `ValidationXmlParserTest` - Tests XML parsing
- `RuleFactoryTest` - Tests rule creation
- `ConditionEvaluatorTest` - Tests condition evaluation

### Integration Tests
- `SpringDtoValidatorIntegrationTest` - Tests end-to-end validation flow with AccountDto

## Examples

See:
- `src/main/resources/validator/platform.xml` - Example production configuration
- `src/test/resources/validator/test.xml` - Example test configuration
- `AccountDto` - Example DTO using `@ValidDto("platform/account")`

## Security Considerations

- XML parsing is secured against XXE attacks
- DOCTYPE declarations are disabled
- External entities are disabled
- Entity expansion is limited

## Benefits

1. **Declarative** - Validation rules are defined in XML, not code
2. **Maintainable** - Easy to modify validation without changing code
3. **Testable** - XML can be validated separately
4. **Extensible** - Easy to add new rules
5. **Clean** - Follows SOLID principles and chain-of-responsibility pattern
6. **Non-invasive** - Doesn't break existing validation logic
