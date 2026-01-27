package in.tech_camp.protospace_b.validation;

import in.tech_camp.protospace_b.validation.ValidationOrder.Position1;
import in.tech_camp.protospace_b.validation.ValidationOrder.Position2;
import jakarta.validation.GroupSequence;


public interface ValidationOrder {
    // Email
    interface Email1 {
    }
    interface Email2 {
    }
    @GroupSequence({Email1.class, Email2.class})
    interface EmailSequence {
    }

    // Password
    interface Password1 {
    }
    interface Password2 {
    }
    @GroupSequence({Password1.class, Password2.class})
    interface PasswordSequence {
    }

    // name
    interface Name1 {
    }
    interface Name2 {
    }
    @GroupSequence({Name1.class, Name2.class})
    interface NameSequence {
    }

    // Profile
    interface Profile1 {
    }
    interface Profile2 {
    }
    @GroupSequence({Profile1.class, Profile2.class})
    interface ProfileSequence {
    }

    // Department
    interface Department1 {
    }
    interface Department2 {
    }
    @GroupSequence({Department1.class, Department2.class})
    interface DepartmentSequence {
    }

    // Position
    interface Position1 {
    }
    interface Position2 {
    }
    @GroupSequence({Position1.class, Position2.class})
    interface PositionSequence {
    }

    // text
    interface text1 {
    }
    interface text2 {
    }
    @GroupSequence({text1.class, text2.class})
    interface textSequence {
    }

    // concept
    interface concept1 {
    }
    interface concept2 {
    }
    @GroupSequence({concept1.class, concept2.class})
    interface conceptSequence {
    }

    // catchCopy
    interface catchCopy1 {
    }
    interface catchCopy2 {
    }
    @GroupSequence({catchCopy1.class, catchCopy2.class})
    interface catchCopySequence {
    }

    interface SecurityQuestion {
    }
    interface SecurityAnswer {
    }



}
