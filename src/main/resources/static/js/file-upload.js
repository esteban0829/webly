window.onload = main;

function main() {
    const form = document.getElementById("file-upload-form");
    const fileInput = document.getElementById("file-input");
    const errorDiv = document.getElementById("file-input-error-msg");
    const csrf = {
        header: document.querySelector('meta[name="_csrf_header"]').content,
        value: document.querySelector('meta[name="_csrf"]').content,
    };
    form.onsubmit = async (e) => {
        e.preventDefault();

        if (fileInput.files.length === 0) {
            errorDiv.innerHTML = 'Select file';
        } else {
            try {
                const file = fileInput.files[0];
                const presignedUrl = await getPresignedUrl(file.name, csrf);
                const response = await uploadFile(presignedUrl, file);
            }
            catch (e) {
                console.error(e);
            }
        }
    }
}

async function getPresignedUrl(filename, csrf) {
    const response = await fetch(
        new Request("/api/v1/files/createPresignedUrl", {
            headers: {
                [csrf.header]: csrf.value
            },
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