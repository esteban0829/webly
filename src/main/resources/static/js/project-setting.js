window.onload = main;

function main() {
    const changeNameButton = document.getElementById("changeNameButton")
    const nameInput = document.getElementById("nameInput")
    const addMemberButton = document.getElementById("addMemberButton")
    const deleteProjectButton = document.getElementById("deleteProjectButton")
    const emailInput = document.getElementById("emailInput")
    const projectAccountsTable = document.getElementById("projectAccountsTable")
    const projectIdAttribute = "data-project-account-id"
    const csrf = {
        header: document.querySelector('meta[name="_csrf_header"]').content,
        value: document.querySelector('meta[name="_csrf"]').content,
    }
    const projectId = getProjectIdFromUrl()

    for (const deleteButton of projectAccountsTable.getElementsByTagName("button")) {
        const projectAccountId = deleteButton.getAttribute(projectIdAttribute)
        deleteButton.onclick = async () => {
            try {
                await deleteMember(projectId, projectAccountId, csrf)
                window.location.reload()
            } catch (e) {
                console.error(e)
            }
        }
    }

    changeNameButton.onclick = async () => {
        try {
            await changeName(projectId, nameInput.value, csrf)
            window.location.reload()
        } catch (e) {
            console.error(e)
        }
    }

    deleteProjectButton.onclick = async () => {
        try {
            await deleteProject(projectId, csrf)
            window.location = '/project'
        } catch (e) {
            console.error(e)
        }
    }

    addMemberButton.onclick = async () => {
        try {
            await addMember(projectId, emailInput.value, csrf)
            window.location.reload()
        } catch (e) {
            console.error(e)
        }
    }

}

function getProjectIdFromUrl() {
    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    return urlParams.get('projectId')
}

function changeName(projectId, newName, csrf) {
    return fetch(`/api/v1/projects/${projectId}/rename`, {
        headers: {
            [csrf.header]: csrf.value
        },
        method: "POST",
        body: newName,
    })
}

function deleteProject(projectId, csrf) {
    return fetch(`/api/v1/projects/${projectId}`, {
        headers: {
            [csrf.header]: csrf.value
        },
        method: "DELETE",
    })
}

function deleteMember(projectId, projectAccountId, csrf) {
    return fetch(`/api/v1/projects/${projectId}/accounts/${projectAccountId}`, {
        headers: {
            [csrf.header]: csrf.value
        },
        method: "DELETE",
    })
}

function addMember(projectId, email, csrf) {
    return fetch(`/api/v1/projects/${projectId}/accounts`, {
        headers: {
            [csrf.header]: csrf.value,
            "Content-Type": "application/json",
        },
        method: "POST",
        body: JSON.stringify({
            email,
            isAdmin: false,
        })
    })
}