package edu.cmu.sv.dialog_act;

import org.openrdf.query.algebra.Str;

import java.util.*;

/**
 * Created by David Cohen on 9/2/14.
 *
 * Define the illocutionary acts for the YODA dialog system
 *
 * Define information to support decision-making by the dialog manager
 *
 * Possibly in the future:
 *   - include templates used for NLG and SLU
 *
 */
public class DialogAct {
    public enum DA_TYPE {ACKNOWLEDGEMENT, NON_HEARING, NON_UNDERSTANDING, ASK_REPEAT, ASK_REPHRASE, DISAMBIGUATE_ROLE,
        DISAMBIGUATE_VALUE, REQUEST_CONFIRM_ROLE, REQUEST_CONFIRM_VALUE}

    // expected *Relative* improvement in different types of confidence
    public static Map<DA_TYPE, Double> expectedJointConfidenceGain = new HashMap<>();
    public static Map<DA_TYPE, Double> expectedMarginalConfidenceGain = new HashMap<>();
    public static Map<DA_TYPE, Double> expectedRoleConfidenceGain = new HashMap<>();
    public static Map<DA_TYPE, Double> expectedConfirmationConfidenceGain = new HashMap<>();

    // define dialogActContentSpec of different DA_TYPES
    public static Map<DA_TYPE, Map<String, String>> dialogActContentSpec = new HashMap<>();

    static {
        //// Define expected improvements in information from clarification dialog acts
        // these dialog acts are expected to improve joint confidence
        expectedJointConfidenceGain.put(DA_TYPE.NON_HEARING, .6);
        expectedJointConfidenceGain.put(DA_TYPE.NON_UNDERSTANDING, .6);
        expectedJointConfidenceGain.put(DA_TYPE.ASK_REPEAT, .6);
        expectedJointConfidenceGain.put(DA_TYPE.ASK_REPHRASE, .6);

        // these dialog acts are expected to improve marginal confidence
        expectedMarginalConfidenceGain.put(DA_TYPE.REQUEST_CONFIRM_VALUE, .6);
        expectedMarginalConfidenceGain.put(DA_TYPE.DISAMBIGUATE_VALUE, .6);

        // these dialog acts are expected to decrease ambiguity about role membership
        expectedRoleConfidenceGain.put(DA_TYPE.DISAMBIGUATE_ROLE, .6);
        expectedRoleConfidenceGain.put(DA_TYPE.REQUEST_CONFIRM_ROLE, .6);

        // expected to cause confirmation (sometimes required, can dramatically reduce uncertainty)
        expectedConfirmationConfidenceGain.put(DA_TYPE.REQUEST_CONFIRM_VALUE, .6);


        //// Define DA dialogActContentSpec
        Map twoRoles = new HashMap();
        twoRoles.put("r1", "role");
        twoRoles.put("r2", "role");
        Map oneRole = new HashMap();
        oneRole.put("r1", "role");
        Map twoValues = new HashMap();
        twoValues.put("v1", "value");
        twoValues.put("v2", "value");
        Map oneValue = new HashMap();
        oneValue.put("v1", "value");
        dialogActContentSpec.put(DA_TYPE.ACKNOWLEDGEMENT, new HashMap<>());
        dialogActContentSpec.put(DA_TYPE.NON_HEARING, new HashMap<>());
        dialogActContentSpec.put(DA_TYPE.NON_UNDERSTANDING, new HashMap<>());
        dialogActContentSpec.put(DA_TYPE.ASK_REPEAT, new HashMap<>());
        dialogActContentSpec.put(DA_TYPE.ASK_REPHRASE, new HashMap<>());
        dialogActContentSpec.put(DA_TYPE.DISAMBIGUATE_ROLE, twoRoles);
        dialogActContentSpec.put(DA_TYPE.REQUEST_CONFIRM_ROLE, oneRole);
        dialogActContentSpec.put(DA_TYPE.DISAMBIGUATE_VALUE, twoValues);
        dialogActContentSpec.put(DA_TYPE.REQUEST_CONFIRM_VALUE, oneValue);

    }

}
