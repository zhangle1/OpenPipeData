package org.pipeData.core.common;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.pipeData.core.base.exception.Exceptions;
import org.springframework.util.CollectionUtils;

import java.util.Set;
import java.util.StringJoiner;

public class BeanUtils {

    private static final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();

    public static void requireNotNull(Object obj, String... fields) {
        for (String field : fields) {
            Object fieldValue = ReflectUtils.getFieldValue(obj, field);
            if (fieldValue == null) {
                Exceptions.msg("field " + field + " can not be null");
            }
        }
    }

    public static void validate(Object obj, Class<?>... groups) {
        Set<ConstraintViolation<Object>> validate = validatorFactory.getValidator().validate(obj, groups);
        if (!CollectionUtils.isEmpty(validate)) {
            StringJoiner message = new StringJoiner(",");
            for (ConstraintViolation<Object> v : validate) {
                message.add(v.getPropertyPath() + ":" + v.getMessage());
            }
            Exceptions.msg(message.toString());
        }
    }

}
