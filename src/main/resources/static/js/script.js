console.log("Script loaded");

let currentTheme = getTheme();
let themeButton = document.getElementById("theme_change_button");

document.addEventListener("DOMContentLoaded", function() {
    console.log("DOM fully loaded and parsed");
    changeTheme(currentTheme);
});

// change theme
function changeTheme(currentTheme){
    // set the current theme to html
    document.querySelector("html").classList.add(currentTheme);
    // change the text of the button
    themeButton.querySelector("span").textContent = currentTheme === "light" ? "Dark" : "Light";

    // add event listener to the theme change button
    themeButton.addEventListener("click", function(){
        console.log("Theme change button clicked");
        if(currentTheme === "light"){
            // set the current theme to html page
            document.querySelector("html").classList.replace("light", "dark");
            currentTheme = "dark";
        } else {
            // set the current theme to html page
            document.querySelector("html").classList.replace("dark", "light");
            currentTheme = "light";
        }
        // change the text of the button
        themeButton.querySelector("span").textContent = currentTheme === "light" ? "Dark" : "Light";
        // save the current theme to localstorage
        setTheme(currentTheme);
    });

}

// set theme to lacalstorage
function setTheme(theme){
    localStorage.setItem("theme", theme);
}

// get theme from localstorage
function getTheme(){
    let theme = localStorage.getItem("theme");
    return theme ? theme : "light";
}