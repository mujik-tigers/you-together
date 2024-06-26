local roomIndex = KEYS[1]
local userIndex = KEYS[2]
local uniqueNicknameSet = KEYS[3]

local eraseRoomList = redis.call("FT.SEARCH", roomIndex, "@activate:{false}", "LIMIT", 0, 10000, "NOCONTENT")
for i = 2, #eraseRoomList do
    redis.call("DEL", eraseRoomList[i])
    redis.call("DEL", "playlist:" .. string.match(eraseRoomList[i], "([^:]+)$"))
    redis.call("DEL", "chat:" .. string.match(eraseRoomList[i], "([^:]+)$"))
end

local inactiveRoomList = redis.call("FT.SEARCH", roomIndex, "@participantCount:[0 0]", "LIMIT", 0, 10000, "NOCONTENT")
for i = 2, #inactiveRoomList do
    redis.call("JSON.SET", inactiveRoomList[i], "$.activate", "false")
end

local userList = redis.call("KEYS", "user:*")
for _, key in ipairs(userList) do
    local jsonData = redis.call("JSON.GET", key)
    local user = cjson.decode(jsonData)
    if user.activate == false then
        redis.call("DEL", key)
        redis.call("SREM", uniqueNicknameSet, user.nickname)
    elseif user.currentRoomCode == nil then
        redis.call("JSON.SET", key, "$.activate", "false")
    end
end

return eraseRoomList

