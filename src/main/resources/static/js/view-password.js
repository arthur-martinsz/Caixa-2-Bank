$('.view-password').click(() => {
    let attr = $('.password').attr('type');

    if ('password' == attr) {
        $('.password').attr('type', 'text');
    } else {
        $('.password').attr('type', 'password');
    }
});


