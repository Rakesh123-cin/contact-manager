console.log("Contacts.js");

// script for viewing contact details in modal

const viewContactModal = document.getElementById("view_contact_modal");

// options with default values
const options = {
    placement: 'bottom-right',
    backdrop: 'dynamic',
    backdropClasses:
        'bg-gray-900/50 dark:bg-gray-900/80 fixed inset-0 z-40',
    closable: true,
    onHide: () => {
        console.log('modal is hidden');
    },
    onShow: () => {
        console.log('modal is shown');
    },
    onToggle: () => {
        console.log('modal has been toggled');
    },
};

// instance options object
const instanceOptions = {
  id: 'view_contact_modal',
  override: true
};

const contactModal = new Modal(viewContactModal, options, instanceOptions);

function showContactModal()
{
    contactModal.show();
}

function hideContactModal()
{
    contactModal.hide();
}

async function loadContactData(id)
{
    try{
        const data = await((await fetch(`/api/contacts/${id}`)).json());
        console.log(data);
        document.getElementById("contact_name").textContent = data.name;
        document.getElementById("contact_image").src = data.picture;
        document.getElementById("contact_email").textContent = data.email;
        document.getElementById("contact_phone").textContent = data.phoneNumber;
        document.getElementById("contact_address").textContent = data.address;
        document.getElementById("contact_description").textContent = data.description;
        document.getElementById("contact_favourite").textContent = data.favorite ? "★".repeat(5*data.favorite) : "Contact Not marked as Favourite";
        document.getElementById("contact_instagram").href = data.instagramLink ? data.instagramLink : "#";
        document.getElementById("contact_linkedin").href = data.linkedInLink ? data.linkedInLink : "#";
        // show the modal
        showContactModal(); 
    }catch(error)
    {
        console.error("Error loading contact data: ", error);
    }
}

// script for deleting contact
function deleteContact(contactId)
{
    Swal.fire({
    icon: "warning",
    title: "Do you want to delete the Contact?",
    showCancelButton: true,
    confirmButtonText: "Delete",
    }).then((result) => {
    if (result.isConfirmed) {
        const deleteUrl = `/user/contacts/delete/${contactId}`;
        window.location.href = deleteUrl;
    }
    });
}

// script for exporting contact data to excel
function exportData()
{
    const table = document.querySelector("table"); // your contacts table
    const rows = table.querySelectorAll("tbody tr");

    let data = [];
    
    // Excel Header
    data.push(["Name", "Email", "Phone"]);

    rows.forEach(row => {
        const cols = row.querySelectorAll("td, th");

        // NAME & EMAIL (inside first column)
        const name = cols[0].querySelector(".text-base")?.innerText || "";
        const email = cols[0].querySelector(".font-normal")?.innerText || "";

        // PHONE (second column)
        const phone = cols[1].innerText.replace(/\D/g, ""); // removes icons

        data.push([name, email, phone]);
    });

    // Create Excel Sheet
    const worksheet = XLSX.utils.aoa_to_sheet(data);
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, "Contacts");

    XLSX.writeFile(workbook, "contacts.xlsx");
}