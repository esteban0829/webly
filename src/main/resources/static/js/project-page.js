window.onload = main;

async function main() {
    const addFolderButton = document.getElementById('addFolderButton')
    const addLinkButton = document.getElementById('addLinkButton')
    const deleteButton = document.getElementById('deleteButton')
    const renameButton = document.getElementById('renameButton')
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
                    const folderId = toOriginId(node.id)
                    return node.id === '#'
                        ? `/api/v1/projects/${projectId}/folders`
                        : `/api/v1/projects/${projectId}/folders/${folderId}`
                },
                dataFilter: (nodes) => {
                    const jsonNode = JSON.parse(nodes)
                    let data = null
                    if (typeof(jsonNode.childFolders) !== 'undefined' &&
                        typeof(jsonNode.childLinks) !== 'undefined') {
                        const folders = jsonNode.childFolders.map(x => ({
                            id: toFolderId(x.id),
                            children: true,
                            text: x.name,
                            parentId: toFolderId(x.parentId)
                        }))
                        const links = jsonNode.childLinks.map(x => ({
                            id: toLinkId(x.id),
                            icon: "jstree-file",
                            children: false,
                            text: x.name,
                            parentId: toFolderId(x.folderId)
                        }))
                        data = folders.concat(links)
                    } else {
                        data = jsonNode.map(x => ({
                            id: toFolderId(x.id),
                            children: true,
                            text: x.name,
                            parentId: toFolderId(x.parentId),
                        }))
                    }
                    console.log('dataFilter')
                    console.log(data)
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
            if (isLink(node)) {
                const parentId = $tree.jstree(true).get_parent(node)
                await moveLink(projectId, toOriginId(parentId), toOriginId(node), toOriginId(target.id), csrf)
            } else {
                await moveFolder(projectId, toOriginId(node), toOriginId(target.id), csrf)
            }
        }
    })

    _ = (async () => {

        while (true) {
            await sleep(1000);
            try {
                const newActionLogId = await getRecentActionLogId(projectId, csrf)
                if (lastActionLogId === newActionLogId) continue;

                const actionLogs = await getActionLogs(projectId, lastActionLogId, csrf)
                lastActionLogId = Math.max(newActionLogId, ...actionLogs.map(x => x.id))
                console.log('actionLogs')
                console.log(actionLogs)
                enableUpdate = true
                for (const log of actionLogs) {
                    console.log(log)
                    if (log.actionType === 'CREATE_FOLDER') {
                        const newFolder = {
                            id: toFolderId(log.folderId),
                            children: true,
                            text: log.newName,
                            parentId: toFolderId(log.parentId),
                        }
                        const node = $tree.jstree(true).get_node(newFolder.parentId)
                        if (node === false || !node.state.loaded) continue
                        $tree.jstree(true).create_node(newFolder.parentId, newFolder)
                    }
                    else if (log.actionType === 'DELETE_FOLDER') {
                        const folderId = toFolderId(log.folderId)
                        $tree.jstree(true).delete_node(folderId)
                    }
                    else if (log.actionType === 'RENAME_FOLDER') {
                        $tree.jstree(true).rename_node(toFolderId(log.folderId), log.newName)
                    }
                    else if (log.actionType === 'MOVE_FOLDER') {
                        const folderId = toFolderId(log.folderId)
                        const oldNode = $tree.jstree(true).get_node(folderId)
                        $tree.jstree(true).delete_node(folderId)

                        if (oldNode === false) continue

                        const newFolder = {
                            id: folderId,
                            children: true,
                            text: oldNode.text,
                            parentId: toFolderId(log.toFolderId),
                        }
                        const parentNode = $tree.jstree(true).get_node(newFolder.parentId)
                        if (parentNode === false || !parentNode.state.loaded) continue
                        $tree.jstree(true).create_node(newFolder.parentId, newFolder)
                    }
                    else if (log.actionType === 'CREATE_LINK') {
                        const newLink = {
                            id: toLinkId(log.linkId),
                            icon: "jstree-file",
                            children: false,
                            text: log.newName,
                            parentId: toFolderId(log.parentId),
                        }
                        const node = $tree.jstree(true).get_node(newLink.parentId)
                        if (node === false || !node.state.loaded) continue
                        $tree.jstree(true).create_node(newLink.parentId, newLink)
                    }
                    else if (log.actionType === 'DELETE_LINK') {
                        const linkId = toLinkId(log.linkId)
                        $tree.jstree(true).delete_node(linkId)
                    }
                    else if (log.actionType === 'RENAME_LINK') {
                        $tree.jstree(true).rename_node(toLinkId(log.linkId), log.newName)
                    }
                    else if (log.actionType === 'MOVE_LINK') {
                        const linkId = toLinkId(log.linkId)
                        const oldNode = $tree.jstree(true).get_node(linkId)
                        $tree.jstree(true).delete_node(linkId)

                        if (oldNode === false) continue

                        const newLink = {
                            id: linkId,
                            icon: "jstree-file",
                            children: false,
                            text: oldNode.text,
                            parentId: toFolderId(log.toFolderId),
                        }
                        const parentNode = $tree.jstree(true).get_node(newLink.parentId)
                        if (parentNode === false || !parentNode.state.loaded) continue
                        $tree.jstree(true).create_node(newLink.parentId, newLink)
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
            await addFolder(projectId, folderName, toOriginId(parentId), csrf)
        } catch (e) {
            console.error(e)
        }
    }

    addLinkButton.onclick = async () => {
        const linkName = window.prompt('Link name')
        if (!linkName) return;
        const url = window.prompt('Url')
        if (!url) return;

        const selectedNodes = $tree.jstree('get_selected')
        if (selectedNodes.length !== 1) return;
        const parentId = selectedNodes[0]

        try {
            await addLink(projectId, toOriginId(parentId), linkName, url, csrf)
        } catch (e) {
            console.error(e)
        }
    }

    deleteButton.onclick = async () => {
        const selectedNodes = $tree.jstree('get_selected')
        console.log(selectedNodes)
        for (const node of selectedNodes) {
            try {
                if (isLink(node)) {
                    const parentId = $tree.jstree(true).get_parent(node)
                    await deleteLink(projectId, toOriginId(parentId), toOriginId(node), csrf)
                } else {
                    await deleteFolder(projectId, toOriginId(node), csrf)
                }
            } catch (e) {
                console.error(e)
            }
        }
    }

    renameButton.onclick = async () => {
        const selectedNodes = $tree.jstree('get_selected')
        if (selectedNodes.length !== 1) return;
        const nodeId = selectedNodes[0]

        const newName = window.prompt('New name')
        if (!newName) return;

        try {
            if (isLink(nodeId)) {
                const parentId = $tree.jstree(true).get_parent(nodeId)
                await renameLink(projectId, toOriginId(parentId), toOriginId(nodeId), newName, csrf)
            } else {
                await renameFolder(projectId, toOriginId(nodeId), newName, csrf)
            }
        } catch (e) {
            console.error(e)
        }
    }
}

const folderIdPrefix = 'folder-'
const linkIdPrefix = 'link-'

function toOriginId(id) {
    if (id === null) return null
    if (id.startsWith(folderIdPrefix)) {
        return id.substr(folderIdPrefix.length)
    }
    if (id.startsWith(linkIdPrefix)) {
        return id.substr(linkIdPrefix.length)
    }
    return null
}

function toFolderId(id) {
    if (id === null) return '#'
    return 'folder-' + id
}

function toLinkId(id) {
    return 'link-' + id
}

function isLink(id) {
    return id !== null && id.startsWith(linkIdPrefix)
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

function addLink(projectId, folderId, name, url, csrf) {
    return fetch(`/api/v1/projects/${projectId}/folders/${folderId}/links`, {
        headers: {
            [csrf.header]: csrf.value,
            "Content-Type": "application/json",
        },
        method: "POST",
        body: JSON.stringify({
            name,
            url,
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

function deleteLink(projectId, folderId, linkId, csrf) {
    return fetch(`/api/v1/projects/${projectId}/folders/${folderId}/links/${linkId}`, {
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

function renameLink(projectId, folderId, linkId, newName, csrf) {
    return fetch(`/api/v1/projects/${projectId}/folders/${folderId}/links/${linkId}/rename`, {
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

function moveLink(projectId, folderId, linkId, targetParentId, csrf) {
    return fetch(`/api/v1/projects/${projectId}/folders/${folderId}/links/${linkId}/move`, {
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