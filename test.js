const math = require('mathjs');
const Discord = require('discord.js');
const client = new Discord.Client();
const auth = require('./auth.json');
const odpoved = require("./answers.json");
const Arothe = "<@119855451994193920>";
const mysql = require("mysql");
const version = "1.2.2";
const channelMajn = "354235259723841538";
const channelDev = "542070256320118815";
const badassDev = false;


var allowBadass = true;
var inDev = false;
var sleeping = false;

var timer = false;
var startTime;
var currentTime;
var runningTimer;


var lastMsgTime = new Date;


function badass() {
    return (allowBadass && ((new Date().getHours() >= 22) || (new Date().getHours() < 6) || (badassDev)));
}

function voteTimer() {
    if (badass()) {
        client.channels.get(channelMajn).send("&2Hej, ty! Hlasuj pro mě! &4/vote");
    } else {
        client.channels.get(channelMajn).send("&2Nezapomeňte hlasovat pro server! &4/vote");
    }
}


client.on('ready', () = > {
    console.log('Jsem připravena pomáhat :)');
client.user.setActivity('Helping on Majncraft.cz');
console.log("Naslouchám :)");
console.log("");
setInterval(voteTimer, 1 * 60 * 60 * 1000);

})
;


// Vytvoření spojení s DB
var db = mysql.createConnection({
    host: auth.DBhost,
    user: auth.DBuser,
    password: auth.DBpassword,
    database: "FQbot"
});


client.on('message', function (message) {
    var mc;

    if (message.content.includes("]") && message.content.includes("/")) {
        var username = (message.content.split("/"))
        if (username[2]) {
            username = (username[2]).split("]")[0].split("*").join(""); // Vytažení jména z bloku [Tethys/Global/name]
            username = username.charAt(0).toUpperCase() + username.substr(1);
        } else {
            username = undefined
        }

    } else {
        if (message.member !== null) {
            username = message.member.displayName;
        } else {
            username = undefined;
        }
    }


    // Odstrani diakritiku, velka pismena a hvezdicky
    mc = contentCleanup().normalize('NFD').replace(/[\u0300-\u036f]/g, "").toLowerCase();


    // ===== FUNKCE =====


    function reload() { // funkce pro restart
        console.log('Restartuji...');
        client.destroy().then(() = > client.login(auth.token)
    )
        ;
    }


    function kill() { // funkce pro restart
        console.log('Au.');
        client.destroy();
        process.exit();
    }


    function mci(str) { // Zkrácení zápisu

        return mc.includes(str);
    }


    function getRandomFromArray(arr) {
        var arrSize = arr.length;
        var rnd = Math.floor(Math.random() * arrSize);

        return arr[rnd];
    }


    function getTime() { // Vytažení aktuálního času
        var date = new Date();

        var hodiny = date.getHours();
        var minuty = date.getMinutes();
        var sekundy = date.getSeconds();

        if (hodiny < 10) {
            hodiny = "0" + hodiny;
        }
        if (minuty < 10) {
            minuty = "0" + minuty;
        }
        if (sekundy < 10) {
            sekundy = "0" + sekundy;
        }
        return (hodiny + ":" + minuty + ":" + sekundy);
    }


    function dev(devmode) {  // Dev mode
        if (devmode) {
            return message.content.normalize('NFD').replace(/[\u0300-\u036f]/g, "").toLowerCase().includes("<%>");
        } else {
            return message.content.normalize('NFD').replace(/[\u0300-\u036f]/g, "").toLowerCase().includes("help");
        }
    }


    function toggleDevMode() { // Přepínání devmode
        if (inDev) {
            console.log("Vypínám DevMode.");
            inDev = false;
        } else {
            console.log("Zapínám DevMode.");
            inDev = true;
        }
    }

    function toggleBadass() { // Přepínání devmode
        if (allowBadass) {
            console.log("Aww :/");
        } else {
            console.log("Let's party boyz!");
        }
        allowBadass = !allowBadass;
    }


    function contentCleanup() { // Očištění zprávy ([T/G/N] msg -> msg)
        if (message.content.includes("]")) {
            return ((message.content.split("]")[1]).trim());
        } else {
            return message.content;
        }
    }


    function echo(msg) { // Konzolová zpráva co Evelynn poslala
        lastMsgTime = new Date;
        console.log("-----");
        console.log("Sent: " + msg);
        console.log("-----");
        message.channel.send(msg);
    }


    function isNumber(n) {
        return !isNaN(parseFloat(n)) && isFinite(n);
    }

    function toggleSleep() {
        sleeping = !sleeping;
    }


    // ===== KONEC FUNKCÍ =====


    if ((message.author.id !== client.user.id)) { // loguj zprávu, pokud jsi ji nenapsala sama

        if (!message.content.includes("[")) {
            if (message.member !== null) {
                console.log(message.member.displayName + ": " + mc);
            }
        } else {
            console.log(message.content);
        }

    }

    // ====== Admin commandy ======


    if (mc[0] == ">") {
        // Restart bota -> Naslouchá jen určitým id z discordu
        if ((mc == ">reboot") && ((auth.ops.indexOf(message.author.id) >= 0) || (auth.mods.indexOf(message.author.id) >= 0))) {
            reload(); // Restartuje bota, neobnoví soubor -> ani po přepsání kódu nenačte nový blok
        }
        if ((mc == ">kill") && (auth.ops.indexOf(message.author.id) >= 0)) {
            kill(); // Restartuje bota, neobnoví soubor -> ani po přepsání kódu nenačte nový blok
        } else if ((mc == ">devmode") && (auth.ops.indexOf(message.author.id) >= 0)) {
            toggleDevMode(); // Přepíná developer mod
        } else if ((mc == ">badass") && ((auth.ops.indexOf(message.author.id) || (auth.ops.indexOf(message.author.id) >= 0) >= 0))) {
            toggleBadass(); // Přepíná developer mod
        } else if ((mc == ">devmode?") && (auth.ops.indexOf(message.author.id) >= 0)) {
            if (inDev) { // Vrátí stav developer módu
                echo("Developer mode je zapnutý.");
            } else {
                echo("Developer mode je vypnutý.");
            }
        } else if (mc == ">version") {
            echo("Právě jsem na verzi " + version);
        } else if (mc == ">docs") {
            echo("Zde je má dokumentace: https://forum.majncraft.cz/threads/10734/");
        } else if (mc == ">getops") {
            console.log(message.member.displayName + ": " + message.author.id);
            echo(Arothe + ", " + message.member.displayName + " chce oprávnění!");
            // hajlajtne mě a pošle userID z discordu
        } else if ((mc == ">sleep") && (auth.ops.indexOf(message.author.id) >= 0)) {
            toggleSleep(); // Přepíná sleep mod
        }

    }
    // ====== Konec admin commandů ======

    if (!sleeping) {


        // kolik je hodin pro tweetyho
        if (mci("kolik je hodin")) {
            if (badass()) {
                echo("Dost na to, abych tu seděla bez trička. A tobě nezbývá, než si představovat, jak krásný pohled to je.");
            } else {
                echo(odpoved.general.cas + getTime());
            }
        } else if (mci("kolikateho je") || mci("jake je datum")) {
            if (badass()) {
                echo("Pff, koho to zajímá... zítra je pondělí.");
            } else {
                var months = ["Leden", "Únor", "Březen", "Duben", "Květen", "Červen", "Červenec", "Srpen", "Září", "Říjen", "Listopad", "Prosinec"];
                var date = new Date();
                echo("Dnes je " + date.getDate() + ". " + months[date.getMonth()] + " " + date.getFullYear());
            }
        } else if (mci("kdo je online")) {
            if (badass()) {
                echo("Nikdo důležitý... a já.");
            } else {
                echo("playerlist");
            }
        } else if (mci("joined lobby for the first time") && mci(":tada:") && message.author.id == 354236347869036545) {
            var msg = message.content.replace("\_", "_").split("*").join("").split(" ");
            echo(odpoved.general.welcome[0] + "&e" + msg[1] + "&r" + odpoved.general.welcome[1]);


        }


        // Vypočítej

        else if (mc.substr(0, 9) == "vypocitej" || mc.substr(0, 8) == "spocitej" || mc.substr(0, 8) == "kolik je") {
            if (mc.substr(0, 9) == "vypocitej") {
                var priklad = mc.substr(9, mc.length);
            } else {
                var priklad = mc.substr(8, mc.length);
            }


            priklad = priklad.replace("x", "*").replace(":", "/").replace("**", "^").replace(",", ".").replace("×", "*").replace("?", "").replace("v", "sqrt");
            //priklad = priklad.replace(/[^0-9.*\-+()!/^]/g, "");

            console.log("=================");
            console.log(priklad);
            console.log("=================");

            if (badass()) {
                echo("Už se mi nechce, použij kalkulačku.");
            } else {
                try {

                    var vypocet = math.eval(priklad);
                    vypocet = Math.round(vypocet * 100) / 100;
                    if ((vypocet > 64) && (vypocet !== Infinity) && (!isNaN(vypocet))) {
                        if (vypocet % 64 != 0) {
                            echo("Výsledek je " + vypocet + " což je " + Math.floor(vypocet / 64) + " x 64 + " + Math.round((vypocet % 64) * 100) / 100);
                        } else {
                            echo("Výsledek je " + vypocet + " což je " + Math.floor(vypocet / 64) + " x 64");
                        }
                    } else if ((vypocet !== Infinity) && (!isNaN(vypocet))) {
                        echo("Výsledek je " + vypocet);
                    } else {
                        echo(odpoved.vypocitej.pako);
                    }
                } catch (err) {

                    echo(odpoved.vypocitej.nerozumim);
                }
            }

        }

        //portal

        else if (mc.substr(0, 6) == "portal") {
            var x = 0;
            var z = 0;
            var arr = mc.split(" ");
            var tping = false;


            if (arr[1] && arr[2] && arr[3]) {
                x = Number(arr[1]);
                z = Number(arr[3]);
                tping = true;
            } else if (arr[1] && arr[2] && !arr[3]) {
                x = Number(arr[1]);
                z = Number(arr[2]);
                tping = true;
            } else if (mc == "portal") {
                echo(odpoved.portal.tutorial);
            } else {
                echo(odpoved.portal.nope);
            }


            if (tping == true) {
                var y = 60;
                if (isNumber(x) && isNumber(z)) {
                    x = Math.floor(x / 8);
                    z = Math.floor(z / 8);
                    echo("Svůj portál postav na souřadnicích x=" + x + " y=" + y + " z=" + z);
                } else {
                    console.log("x: " + x.isNumber);
                    console.log(x);
                    console.log("z: " + z.isNumber);
                    console.log(z);
                    echo(odpoved.portal.vtipalek);
                }
            }


        } else if (mc == "zapni casovac" || mc == "vypni casovac" || mc.replace("?", "") == "aktualni cas") {
            if (mc == "zapni casovac") {
                if (timer) {
                    echo("Promiň, ale časovač teď má spuštěný někdo jiný. Zkus to později.");
                } else {
                    echo("Zapínám časovač.");
                    let user = username;
                    startTime = parseInt(new Date().getTime());
                    console.log("start: " + startTime);
                    timer = true;

                    runningTimer = setInterval(() = > {
                        let currentTime = new Date().getTime();
                    if (currentTime - startTime > 600000) {
                        clearInterval(runningTimer);
                        timer = false;
                        startTime = null;
                        echo("Přesáhl jsi deset minut " + user + ". Zastavuji časovač.");
                    }
                },
                    10000
                )
                }
            } else if (mc == "vypni casovac") {
                if (timer) {
                    currentTime = parseInt(new Date().getTime());
                    console.log("current: " + currentTime);
                    clearInterval(runningTimer);
                    var resultTime = currentTime - startTime;
                    console.log(resultTime);

                    if (resultTime > 60000) {
                        echo("Konečný čas je " + ~~(resultTime / 60000) +
                            ((~~(resultTime / 60000) > 4) ? " minut a " : (~~(resultTime / 60000) > 1) ? " minuty a " : " minuta a ")
                            + (~~(resultTime / 1000) % 60) +
                            ((~~(resultTime / 1000) % 60 > 4) ? " vteřin." : (~~(resultTime / 1000) % 60 > 1) ? " vteřiny." : " vteřina."));
                    } else {
                        echo("Konečný čas je " + ~~(resultTime / 1000) + ((~~(resultTime / 1000) > 4) ? " vteřin." : (~~(resultTime / 1000) > 1) ? " vteřiny." : " vteřina."));
                    }

                    timer = false;
                } else {
                    echo("Žádný časovač neběží.");
                }

            } else {
                currentTime = parseInt(new Date().getTime());
                var resultTime = currentTime - startTime;
                if (resultTime > 60000) {
                    echo("Aktuální čas je " + ~~(resultTime / 60000) +
                        ((~~(resultTime / 60000) > 4) ? " minut a " : (~~(resultTime / 60000) > 1) ? " minuty a " : " minuta a ")
                        + (~~(resultTime / 1000) % 60) +
                        ((~~(resultTime / 1000) % 60 > 4) ? " vteřin." : (~~(resultTime / 1000) % 60 > 1) ? " vteřiny." : " vteřina."));
                } else {
                    echo("Aktuální čas je " + ~~(resultTime / 1000) + ((~~(resultTime / 1000) > 4) ? " vteřin." : (~~(resultTime / 1000) > 1) ? " vteřiny." : " vteřina."));
                }
            }


        }

        // Reakce na "miluji te"
        else if ((message.author.id !== client.user.id) && (mci("miluji te") || mci("miluju te")) && (mci("evelynn")) && (!mci("mas me rada"))) {
            var reakce = odpoved.general.reakceNaLasku;
            var rnd = Math.floor(Math.random() * reakce.length);

            echo(reakce[rnd]);
        }

        // Reakce na "mas me rada"
        else if ((message.author.id !== client.user.id) && (mci("mas me rada")) && (mci("evelynn"))) {
            var reakce = odpoved.general.reakceNaOblibenost;
            var rnd = Math.floor(Math.random() * reakce.length);

            echo(reakce[rnd]);
        }

        // Reakce na "jak se mas"
        else if ((message.author.id !== client.user.id) && (mci("jak se mas")) && (mci("evelynn"))) {
            var reakce = odpoved.general.nalada;
            var answer;
            var time = new Date;
            console.log(time.getHours());


            if (time.getHours() >= 11 && time.getHours() <= 12) {
                answer = reakce[0];
            } else if ((time.getHours() >= 20) && (time.getHours() <= 21)) {
                answer = reakce[1];
            } else if ((time.getHours() < 8) && (time.getHours() >= 6)) {
                answer = reakce[2];
            } else if ((time.getHours() < 6)) {
                answer = reakce[7];
            } else if (time.getHours() >= 22) {
                answer = reakce[6];
            } else if (Math.abs((lastMsgTime.getTime() - time.getTime())) > 1800000) {
                answer = reakce[3];
            } else if (Math.abs((lastMsgTime.getTime() - time.getTime())) < 15000) {
                answer = reakce[4];
            } else {
                answer = reakce[5];
            }
            echo(answer);

        }


        //reakce na pozdrav
        else if ((message.author.id !== client.user.id) && (mci("evelynn")) && mc.replace("?", "") != "evelynn") {
            var pozdravy = odpoved.general.reakceNaPozdrav;
            var rnd = Math.floor(Math.random() * pozdravy.length);

            if (username) {

                if (odpoved.general.pozdravy.indexOf((mc.split("evelynn")[0]).trim().replace(",", "")) >= 0) {
                    echo(odpoved.general.reakceNaPozdrav[rnd] + ", " + username);
                } else if (mc.split("evelynn")[0]) {
                    if ((mc.split("evelynn")[0].trim().replace(",", "")).includes("brou noc")) {
                        echo("Dobrou noc i tobě, " + username + " :)");
                    }
                }
            } else {
                if (odpoved.general.pozdravy.indexOf(((mc.split("evelynn")[0]).trim()).replace(",", "")) >= 0) {
                    echo(odpoved.general.reakceNaPozdrav[rnd] + ", " + message.member.displayName);
                    console.log(odpoved.general.pozdravy.indexOf((mc.split("evelynn")[0]).trim()));
                } else if (mc.split("evelynn")[0]) {
                    if ((((mc.split("evelynn")[0]).trim()).replace(",", "")).includes("dobrou noc")) {
                        echo("Krásné sny, " + message.member.displayName);
                    }
                }

            }
        }




        // Counter sognus
        else if ((message.author.id !== client.user.id) && (mc == "ping")) {
            echo("Pong.");
        } else if ((message.author.id !== client.user.name) && dev(inDev)) {

            var helpQuery = 'SELECT * from klicova_slova INNER JOIN ks_o ON klicova_slova.PK_Idks = ks_o.FK_idks INNER JOIN odpoved ON ks_o.FK_ido = odpoved.PK_ido WHERE MATCH(klicova_slova.text) AGAINST ("' + mc.replace("?", "") + '" IN NATURAL LANGUAGE MODE) AND MATCH(odpoved.co) AGAINST ("' + mc.replace("?", "") + '" IN NATURAL LANGUAGE MODE)';
            var badassQuery = 'SELECT * from klicova_slova INNER JOIN ks_o ON klicova_slova.PK_Idks = ks_o.FK_idks INNER JOIN badass ON ks_o.FK_ido = badass.PK_ido WHERE MATCH(klicova_slova.text) AGAINST ("' + mc.replace("?", "") + '" IN NATURAL LANGUAGE MODE) AND MATCH(badass.co) AGAINST ("' + mc.replace("?", "") + '" IN NATURAL LANGUAGE MODE)';
            var query = "";


            // Nastavení badass query
            if (badass()) {
                query = badassQuery;
            } else {
                query = helpQuery;
            }

            db.query(query, function (err, result) {
                if (err) {
                    console.log("MySQL error: " + err);

                    var db = mysql.createConnection({
                        host: auth.DBhost,
                        user: auth.DBuser,
                        password: auth.DBpassword,
                        database: "FQbot"
                    });

                    client.channels.get(channelDev).send("Tak to vypadá, že mi zase spadlo připojení... " + Arothe + " by se na to měl už konečně podívat...");

                }
                if (result) {
                    console.log("Result: %j", result.length);
                    if (result.length == 1) {
                        echo(result[0].text);
                    } else if (result.length > 1) {
                        if (badass()) {
                            echo("Zase něco chceš? Radši běž spát...");
                        } else {
                            echo("Napiš tu otázku lépe");
                        }

                    }
                }


            });
        }
        // LynSis test
        /*else if(mci("evelynn")){
            db.query('SELECT * FROM evelynn WHERE MATCH(evelynn.co) AGAINST ("' + mc + '" IN NATURAL LANGUAGE MODE) > 2' , function (err, result) {
                    if (err){
                        console.log("error");
                    }
                        console.log("Result: %j" , result);
                        if(result.length == 1 ){
                            echo(result[0].text);
                        }else if(result.length > 1 ){
                            echo("Napis tu otazku lepe");
                        }*/
        if ((mc == "evelynn?" || mc == "evelynn")/* && result.length == 0*/) {
            echo("Jak ti mohu pomoci? Prosím, svou otázku napiš do kanálu pomoci. [/h <zpráva>]");
        }/*

                });
        }*/
    }


});
db.connect();
client.login(auth.token);