import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export function passwordStrengthValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const value = control.value;
    if (!value) {
      return null; // Don't validate empty password, let Validators.required handle it
    }

    const hasMinLength = value.length >= 8;
    const hasUppercase = /[A-Z]/.test(value);
    const hasNumber = /\d/.test(value);
    const hasSpecialCharacter = /[!@#$%^&*(),.?":{}|<>]/.test(value);

    const passwordValid =
      hasMinLength && hasUppercase && hasNumber && hasSpecialCharacter;

    return !passwordValid
      ? {
          passwordStrength: {
            hasMinLength: hasMinLength,
            hasUppercase: hasUppercase,
            hasNumber: hasNumber,
            hasSpecialCharacter: hasSpecialCharacter,
          },
        }
      : null;
  };
}
