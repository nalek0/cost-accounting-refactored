function submitForm(event) {
    let formNode = event.target.parentNode;
    let formData = new FormData(formNode);

    if (formData.get("password") &&
            formData.get("password-repeat") &&
            formData.get("password") !== formData.get("password-repeat")) {
        alert("Пароли не совпадают")
        event.preventDefault()
    }
}

window.addEventListener("load", () => {
    [].forEach.call(
        document.querySelectorAll("form.check-form input[type=submit]"),
        element => element.addEventListener("click", submitForm)
    );
});