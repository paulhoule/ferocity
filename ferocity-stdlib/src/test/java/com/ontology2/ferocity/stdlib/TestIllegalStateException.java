package com.ontology2.ferocity.stdlib;

import com.ontology2.ferocity.Literal;
import org.junit.jupiter.api.Test;

import static fierce.java.lang.IllegalStateException𝔣.newIllegalStateExceptionʌString;
import static fierce.java.lang.Throwable𝔣.callGetMessage;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestIllegalStateException {
    @Test
    public void createAnISE() {
        var ise = newIllegalStateExceptionʌString((Literal.of("b0ws3r")));
        var that = ise.evaluateRT();
        assertEquals("b0ws3r", that.getMessage());

        var message = callGetMessage(ise);
        assertEquals("b0ws3r", message.evaluateRT());
    }

}
