window.onload = main;

async function main() {
    const addFolderButton = document.getElementById('addFolderButton')
    const deleteFolderButton = document.getElementById('deleteFolderButton')
    const renameFolderButton = document.getElementById('renameFolderButton')
    const csrf = {
        header: document.querySelector('meta[name="_csrf_header"]').content,
        value: document.querySelector('meta[name="_csrf"]').content,
    }
    const projectId = getProjectIdFromUrl();

    let lastActionLogId = await getRecentActionLogId(projectId, csrf);
    let enableUpdate = false
    const $tree = $('#jstree_div');
    $tree.jstree({
        core: {
            check_callback: () => enableUpdate,
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
                        parentId: x.parentId ?? '#',
                    }))
                    return JSON.stringify(data)
                }
            },
        },
        plugins: ["dnd"],
    })

    $(document).on('dnd_stop.vakata', async (e, data) => {
        const nodes = data.data.nodes
        const target = $tree.jstree(true).get_node(data.event.target)
        for (const node of nodes) {
            await moveFolder(projectId, node, target.id, csrf)
        }
    })

    _ = (async () => {

        while (true) {
            await sleep(1000);
            try {
                const newActionLogId = await getRecentActionLogId(projectId, csrf)
                if (lastActionLogId === newActionLogId) continue;

                const actionLogs = await getActionLogs(projectId, lastActionLogId, csrf)
                lastActionLogId = newActionLogId
                console.log(actionLogs)
                enableUpdate = true
                for (const log of actionLogs) {
                    console.log(log)
                    if (log.actionType === 'CREATE_FOLDER') {
                        const newFolder = {
                            id: log.folderId,
                            children: true,
                            text: log.newName,
                            parentId: log.parentId ?? '#',
                        }
                        const node = $tree.jstree(true).get_node(newFolder.parentId)
                        if (node === false || !node.state.loaded) continue
                        $tree.jstree(true).create_node(newFolder.parentId, newFolder)
                    }
                    else if (log.actionType === 'DELETE_FOLDER') {
                        const folderId = log.folderId
                        $tree.jstree(true).delete_node(folderId)
                    }
                    else if (log.actionType === 'RENAME_FOLDER') {
                        $tree.jstree(true).rename_node(log.folderId, log.newName)
                    }
                    else if (log.actionType === 'MOVE_FOLDER') {
                        const oldNode = $tree.jstree(true).get_node(log.folderId)
                        $tree.jstree(true).delete_node(log.folderId)

                        if (oldNode === false) continue

                        const newFolder = {
                            id: log.folderId,
                            children: true,
                            text: oldNode.text,
                            parentId: log.toFolderId ?? '#',
                        }
                        const parentNode = $tree.jstree(true).get_node(newFolder.parentId)
                        if (parentNode === false || !parentNode.state.loaded) continue
                        $tree.jstree(true).create_node(newFolder.parentId, newFolder)
                    }
                }
            } catch (e) {
                console.error(e);
            } finally {
                enableUpdate = false
            }
        }
    })()

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

function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
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

function moveFolder(projectId, folderId, targetParentId, csrf) {
    return fetch(`/api/v1/projects/${projectId}/folders/${folderId}/move`, {
        headers: {
            [csrf.header]: csrf.value,
            "Content-Type": "application/json",
        },
        method: "POST",
        body: targetParentId,
    })
}

async function getActionLogs(projectId, recentReadActionLogId, csrf) {
    const response = await fetch(`/api/v1/projects/${projectId}/action-logs?recentReadActionLogId=${recentReadActionLogId}`, {
        headers: {
            [csrf.header]: csrf.value,
        },
    })
    return await response.json()
}

async function getRecentActionLogId(projectId, csrf) {
    const response = await fetch(`/api/v1/projects/${projectId}/action-logs/recent-action-logs`, {
        headers: {
            [csrf.header]: csrf.value,
        },
    })
    return await response.json()
}