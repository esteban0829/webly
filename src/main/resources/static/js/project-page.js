window.onload = main;

function main() {
    const addFolderButton = document.getElementById('addFolderButton')
    const deleteFolderButton = document.getElementById('deleteFolderButton')
    const renameFolderButton = document.getElementById('renameFolderButton')
    const csrf = {
        header: document.querySelector('meta[name="_csrf_header"]').content,
        value: document.querySelector('meta[name="_csrf"]').content,
    }
    const projectId = getProjectIdFromUrl();

    const $tree = $('#jstree_div');
    $tree.jstree({
        core: {
            check_callback: true,
            data: {
                url: (node) => {
                    return `/api/v1/projects/${projectId}/folders`
                },
                data: (node) => {
                    if (node.id === '#')
                        return {}

                    return { parentId: node.id }
                },
                dataFilter: (nodes) => {
                    const data = JSON.parse(nodes).map(x => ({
                        id: x.id,
                        children: true,
                        text: x.name,
                        parentId: x.parentId === null ? '#' : x.parentId,
                    }))
                    return JSON.stringify(data)
                }
            },
        },
        plugins: ["contextmenu"],
    })
        .on("delete_node.jstree", async function (e, data) {
            const folderId = data.node.id
            await deleteFolder(projectId, folderId, csrf)
        })
        .on("rename_node.jstree", async function (e, data) {
            const folderId = data.node.id
            await renameFolder(projectId, folderId, data.text, csrf)
        })
        

    addFolderButton.onclick = async () => {
        const folderName = window.prompt('Folder name')
        if (!folderName) return;

        const selectedNodes = $tree.jstree('get_selected')
        if (selectedNodes.length > 1) return;

        let parentId = null
        if (selectedNodes.length === 1) {
            parentId = selectedNodes[0]
        }
        try {
            await addFolder(projectId, folderName, parentId, csrf)
        } catch (e) {
            console.error(e)
        }
    }

    deleteFolderButton.onclick = async () => {
        const selectedNodes = $tree.jstree('get_selected')
        console.log(selectedNodes)
        for (const node of selectedNodes) {
            try {
                await deleteFolder(projectId, node, csrf)
            } catch (e) {
                console.error(e)
            }
        }
    }

    renameFolderButton.onclick = async () => {
        const selectedNodes = $tree.jstree('get_selected')
        if (selectedNodes.length !== 1) return;
        const folderId = selectedNodes[0]

        const newName = window.prompt('New folder name')
        if (!newName) return;

        try {
            await renameFolder(projectId, folderId, newName, csrf)
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

function addFolder(projectId, name, parentId, csrf) {
    return fetch(`/api/v1/projects/${projectId}/folders`, {
        headers: {
            [csrf.header]: csrf.value,
            "Content-Type": "application/json",
        },
        method: "POST",
        body: JSON.stringify({
            name,
            parentId,
        })
    })
}

async function getFolders(projectId, csrf) {
    const response = await fetch(`/api/v1/projects/${projectId}/folders`, {
        headers: {
            [csrf.header]: csrf.value,
        },
    })
    return await response.json()
}

function deleteFolder(projectId, folderId, csrf) {
    return fetch(`/api/v1/projects/${projectId}/folders/${folderId}`, {
        headers: {
            [csrf.header]: csrf.value,
        },
        method: "DELETE",
    })
}

function deleteFile(folderId, fileId, csrf) {
    return fetch(`/api/v1/projects/${projectId}/folders/${folderId}/files/${fileId}`, {
        headers: {
            [csrf.header]: csrf.value,
        },
        method: "DELETE",
    })
}

function renameFolder(projectId, folderId, newName, csrf) {
    return fetch(`/api/v1/projects/${projectId}/folders/${folderId}/rename`, {
        headers: {
            [csrf.header]: csrf.value,
        },
        method: "POST",
        body: newName,
    })
}