window.onload = main;

function main() {
    const form = document.getElementById("file-upload-form");
    const fileInput = document.getElementById("file-input");
    const errorDiv = document.getElementById("file-input-error-msg");

    form.onsubmit = async (e) => {
        e.preventDefault();

        if (fileInput.files.length === 0) {
            errorDiv.innerHTML = 'Select file';
        } else {
            try {
                const file = fileInput.files[0];
                const preassignedUrl = await getPreassignedUrl(file.name);
                const response = await uploadFile(preassignedUrl, file);
            }
            catch (e) {
                console.error(e);
            }
        }
    }
}

async function getPreassignedUrl(filename) {
    const response = await fetch(
        new Request("/api/v1/files/createPreassignedUrl", {
            method: "POST",
            body: filename,
        })
    )
    return response.text();
}

async function uploadFile(url, file) {
    return await fetch(url, {
            method: "PUT",
            headers: new Headers({
                "Content-Type": "application/octet-stream",
            }),
            body: file,
        }
    );
}