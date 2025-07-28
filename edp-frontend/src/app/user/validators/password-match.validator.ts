import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export function passwordMatchValidator(
  passwordControlName: string,
  confirmPasswordControlName: string
): ValidatorFn {
  return (formGroup: AbstractControl): ValidationErrors | null => {
    const passwordControl = formGroup.get(passwordControlName);
    const confirmPasswordControl = formGroup.get(confirmPasswordControlName);

    if (
      !passwordControl ||
      !confirmPasswordControl ||
      !confirmPasswordControl.value
    ) {
      return null; // Don't validate if controls don't exist or confirm password is empty
    }

    // Clear previous passwordMismatch error on confirmPassword if it exists
    if (confirmPasswordControl.hasError('passwordMismatch')) {
      const errors = { ...confirmPasswordControl.errors };
      delete errors['passwordMismatch'];
      confirmPasswordControl.setErrors(
        Object.keys(errors).length ? errors : null
      );
    }

    if (passwordControl.value !== confirmPasswordControl.value) {
      confirmPasswordControl.setErrors({
        ...confirmPasswordControl.errors,
        passwordMismatch: true,
      });
      return { passwordMismatch: true }; // Set error on the form group
    }
    return null;
  };
}
