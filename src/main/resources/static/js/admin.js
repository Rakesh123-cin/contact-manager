// code to preview image before upload
console.log("Admin user");
document.querySelector("#image_file_input").addEventListener("change", function(event){
    const file = event.target.files[0]; 
    if(file){
        const reader = new FileReader();                
        reader.onload = function(e){
            document.querySelector("#contactImagePreview").src = e.target.result;
        }   
        reader.readAsDataURL(file);
    }
});