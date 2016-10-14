/**
*   This function is called when a user clicks on the logout button. It determines which social platform the user logged in with.
*/
function logout()
{
    console.log("Logout");
    /**
    *   Function to delete all cookies
    */
    var cookies = document.cookie.split(";");
    for (var i = 0; i < cookies.length; i++) {
        var cookie = cookies[i];
        var eqPos = cookie.indexOf("=");
        var name = eqPos > -1 ? cookie.substr(0, eqPos) : cookie;
        document.cookie = name + "=;expires=Thu, 01 Jan 1970 00:00:00 GMT";
    }

    localStorage.setItem('nodes', "");
    localStorage.setItem('edges', "");
    localStorage.setItem('parentlist', "");
    localStorage.setItem('pimlist', "");
    
    window.location.assign('/');

} 