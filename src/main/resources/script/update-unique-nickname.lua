local uniqueNicknameSet = KEYS[1]
local oldNickname = ARGV[1]
local newNickname = ARGV[2]

local exists = redis.call('sismember', uniqueNicknameSet, newNickname)

if exists == 1 then
    return false
else
    redis.call('srem', uniqueNicknameSet, oldNickname)
    redis.call('sadd', uniqueNicknameSet, newNickname)
    return true
end
