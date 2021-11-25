create user 'chatUser'@'localhost' identified by 'strongpassword';
grant select, insert, update, execute on secure_chat_db.* to 'chatUser'@'localhost';
flush privileges;

delimiter $$
create trigger hash_password
before insert on user
for each row
begin
	set New.Password = sha2(New.Password, 256); #sha256 otisak lozinke
    set New.Salt = md5(rand()); #128-bitni salt
    set New.Password = sha2(concat(New.Password, New.Salt), 512); #sha256 otisak lozinke + salt pa opet heširano pomoću sha512
end$$
delimiter ;

DELIMITER $$
create procedure check_password(in username varchar(20), in pass varchar(128), out result int)
begin
	declare oldHash varchar(128) default "";
    declare salt varchar(32) default "";
    declare newHash varchar(128) default "";
    declare newHashPom varchar(64) default "";
    set result=-1;
    
    SELECT u.Password INTO oldHash FROM user u WHERE u.Username = username;
    SELECT u.Salt INTO salt FROM user u WHERE u.Username = username;
    
    set newHashPom = sha2(pass, 256);
    set newHash = sha2(concat(newHashPom, salt), 512);
    
    set result = STRCMP(oldHash, newHash);
end$$
DELIMITER ;

CREATE OR REPLACE VIEW conversation AS
SELECT m.Id, m.DateTime, m.Content, m.SenderId, u.Username as Sender, m.ReceiverId, v.Username as Receiver 
FROM (message m inner join user u on m.SenderId=u.Id) inner join user v where m.ReceiverId=v.Id;