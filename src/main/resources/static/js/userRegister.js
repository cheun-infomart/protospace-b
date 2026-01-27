function showStep(step) {
  const step1 = document.getElementById("step-1");
  const step2 = document.getElementById("step-2");

  if (!step1 || !step2) return;

  if (step === 1) {
    step1.style.display = "block";
    step2.style.display = "none";
  } else {
    step1.style.display = "none";
    step2.style.display = "block";
  }
}

document.addEventListener("DOMContentLoaded", function () {
  const form = document.getElementById("userRegister_form");
  if (!form) return;
  const activeStep = parseInt(form.getAttribute("data-user-register-active-step")) || 1;
  showStep(activeStep);
});
