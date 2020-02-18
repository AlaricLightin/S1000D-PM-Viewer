function isLoginValid (login) {
    return /^[A-Za-z0-9_]+$/.test(login);
}

function isPasswordValid (password) {
    return /^[A-Za-z0-9,.!@#$%^&*()-_+=]+$/.test(password);
}

export function validationNameRules() {
    return [
        v => !!v || 'Необходимо задать имя пользователя.',
        v => v && v.length <= 50 || 'Имя пользователя не должно превышать 50 символов.',
        v => isLoginValid(v) || 'Имя пользователя может содержать латинские буквы, цифры и знак подчёркивания.'
    ];
}

export function validationPasswordRules() {
    return [
        v => !!v || 'Необходимо задать пароль.',
        v => isPasswordValid(v) || 'Пароль может содержать латинские буквы, цифры и символы ,.!@#$%^&*()-_+='
    ];
}