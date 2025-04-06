package org.rtsda.android.ui.beliefs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.rtsda.android.databinding.FragmentBeliefsBinding
import org.rtsda.android.databinding.ItemBeliefBinding

data class Verse(
    val reference: String,
    val content: String
)

data class Belief(
    val id: Int,
    val title: String,
    val summary: String,
    val verses: List<Verse>
)

class BeliefsFragment : Fragment() {
    private var _binding: FragmentBeliefsBinding? = null
    private val binding get() = _binding!!

    private val beliefs = listOf(
        Belief(id = 1, title = "The Holy Scriptures", 
            summary = "The Holy Scriptures, Old and New Testaments, are the written Word of God, given by divine inspiration. The inspired authors spoke and wrote as they were moved by the Holy Spirit.",
            verses = listOf(
                Verse("2 Timothy 3:16-17", "All Scripture is God-breathed and is useful for teaching, rebuking, correcting and training in righteousness, so that the servant of God may be thoroughly equipped for every good work."),
                Verse("2 Peter 1:20-21", "Above all, you must understand that no prophecy of Scripture came about by the prophet's own interpretation of things. For prophecy never had its origin in the human will, but prophets, though human, spoke from God as they were carried along by the Holy Spirit."),
                Verse("Psalm 119:105", "Your word is a lamp for my feet, a light on my path."),
                Verse("John 17:17", "Sanctify them by the truth; your word is truth."),
                Verse("Hebrews 4:12", "For the word of God is alive and active. Sharper than any double-edged sword, it penetrates even to dividing soul and spirit, joints and marrow; it judges the thoughts and attitudes of the heart.")
            )),
        
        Belief(id = 2, title = "The Trinity",
            summary = "There is one God: Father, Son, and Holy Spirit, a unity of three coeternal Persons.",
            verses = listOf(
                Verse("Deuteronomy 6:4", "Hear, O Israel: The LORD our God, the LORD is one."),
                Verse("Matthew 28:19", "Therefore go and make disciples of all nations, baptizing them in the name of the Father and of the Son and of the Holy Spirit."),
                Verse("2 Corinthians 13:14", "May the grace of the Lord Jesus Christ, and the love of God, and the fellowship of the Holy Spirit be with you all."),
                Verse("John 1:1-3", "In the beginning was the Word, and the Word was with God, and the Word was God. He was with God in the beginning. Through him all things were made; without him nothing was made that has been made."),
                Verse("Genesis 1:26", "Then God said, 'Let us make mankind in our image, in our likeness...'")
            )),
        
        Belief(id = 3, title = "The Father",
            summary = "God the eternal Father is the Creator, Source, Sustainer, and Sovereign of all creation.",
            verses = listOf(
                Verse("Genesis 1:1", "In the beginning God created the heavens and the earth."),
                Verse("Revelation 4:11", "You are worthy, our Lord and God, to receive glory and honor and power, for you created all things, and by your will they were created and have their being."),
                Verse("1 Corinthians 15:28", "When he has done this, then the Son himself will be made subject to him who put everything under him, so that God may be all in all."),
                Verse("John 3:16", "For God so loved the world that he gave his one and only Son, that whoever believes in him shall not perish but have eternal life."),
                Verse("Psalm 103:13", "As a father has compassion on his children, so the LORD has compassion on those who fear him.")
            )),
        
        Belief(id = 4, title = "The Son",
            summary = "God the eternal Son became incarnate in Jesus Christ. Through Him all things were created, the character of God is revealed, the salvation of humanity is accomplished, and the world is judged.",
            verses = listOf(
                Verse("John 1:1-3", "In the beginning was the Word, and the Word was with God, and the Word was God. He was with God in the beginning. Through him all things were made; without him nothing was made that has been made."),
                Verse("John 1:14", "The Word became flesh and made his dwelling among us. We have seen his glory, the glory of the one and only Son, who came from the Father, full of grace and truth."),
                Verse("Colossians 1:15-19", "The Son is the image of the invisible God, the firstborn over all creation. For in him all things were created: things in heaven and on earth, visible and invisible, whether thrones or powers or rulers or authorities; all things have been created through him and for him. He is before all things, and in him all things hold together."),
                Verse("Hebrews 1:3", "The Son is the radiance of God's glory and the exact representation of his being, sustaining all things by his powerful word. After he had provided purification for sins, he sat down at the right hand of the Majesty in heaven."),
                Verse("Philippians 2:5-8", "In your relationships with one another, have the same mindset as Christ Jesus: Who, being in very nature God, did not consider equality with God something to be used to his own advantage; rather, he made himself nothing by taking the very nature of a servant, being made in human likeness. And being found in appearance as a man, he humbled himself by becoming obedient to death— even death on a cross!")
            )),
        
        Belief(id = 5, title = "The Holy Spirit",
            summary = "God the eternal Spirit was active with the Father and the Son in Creation, incarnation, and redemption.",
            verses = listOf(
                Verse("Genesis 1:1-2", "In the beginning God created the heavens and the earth. Now the earth was formless and empty, darkness was over the surface of the deep, and the Spirit of God was hovering over the waters."),
                Verse("John 14:16-18", "And I will ask the Father, and he will give you another advocate to help you and be with you forever— the Spirit of truth. The world cannot accept him, because it neither sees him nor knows him. But you know him, for he lives with you and will be in you. I will not leave you as orphans; I will come to you."),
                Verse("John 16:7-13", "But very truly I tell you, it is for your good that I am going away. Unless I go away, the Advocate will not come to you; but if I go, I will send him to you. When he comes, he will prove the world to be in the wrong about sin and righteousness and judgment: about sin, because people do not believe in me; about righteousness, because I am going to the Father, where you can see me no longer; and about judgment, because the prince of this world now stands condemned."),
                Verse("Acts 1:8", "But you will receive power when the Holy Spirit comes on you; and you will be my witnesses in Jerusalem, and in all Judea and Samaria, and to the ends of the earth."),
                Verse("Romans 8:26-27", "In the same way, the Spirit helps us in our weakness. We do not know what we ought to pray for, but the Spirit himself intercedes for us through wordless groans. And he who searches our hearts knows the mind of the Spirit, because the Spirit intercedes for God's people in accordance with the will of God.")
            )),
        
        Belief(id = 6, title = "Creation",
            summary = "God is Creator of all things, and has revealed in Scripture the authentic account of His creative activity.",
            verses = listOf(
                Verse("Genesis 1:1-2", "In the beginning God created the heavens and the earth. Now the earth was formless and empty, darkness was over the surface of the deep, and the Spirit of God was hovering over the waters."),
                Verse("Genesis 1:26-27", "Then God said, 'Let us make mankind in our image, in our likeness, so that they may rule over the fish in the sea and the birds in the sky, over the livestock and all the wild animals, and over all the creatures that move along the ground.' So God created mankind in his own image, in the image of God he created them; male and female he created them."),
                Verse("Exodus 20:11", "For in six days the LORD made the heavens and the earth, the sea, and all that is in them, but he rested on the seventh day. Therefore the LORD blessed the Sabbath day and made it holy."),
                Verse("Psalm 19:1-2", "The heavens declare the glory of God; the skies proclaim the work of his hands. Day after day they pour forth speech; night after night they reveal knowledge."),
                Verse("Hebrews 11:3", "By faith we understand that the universe was formed at God's command, so that what is seen was not made out of what was visible.")
            )),
        
        Belief(id = 7, title = "The Nature of Humanity",
            summary = "Man and woman were made in the image of God with individuality, the power and freedom to think and to do.",
            verses = listOf(
                Verse("Genesis 1:26-27", "Then God said, 'Let us make mankind in our image, in our likeness, so that they may rule over the fish in the sea and the birds in the sky, over the livestock and all the wild animals, and over all the creatures that move along the ground.' So God created mankind in his own image, in the image of God he created them; male and female he created them."),
                Verse("Psalm 8:4-6", "What is mankind that you are mindful of them, human beings that you care for them? You have made them a little lower than the angels and crowned them with glory and honor. You made them rulers over the works of your hands; you put everything under their feet."),
                Verse("Genesis 2:7", "Then the LORD God formed a man from the dust of the ground and breathed into his nostrils the breath of life, and the man became a living being."),
                Verse("Genesis 2:15", "The LORD God took the man and put him in the Garden of Eden to work it and take care of it."),
                Verse("Genesis 2:18", "The LORD God said, 'It is not good for the man to be alone. I will make a helper suitable for him.'")
            )),
        
        Belief(id = 8, title = "The Great Controversy",
            summary = "All humanity is now involved in a great controversy between Christ and Satan regarding the character of God, His law, and His sovereignty over the universe.",
            verses = listOf(
                Verse("Revelation 12:7-9", "Then war broke out in heaven. Michael and his angels fought against the dragon, and the dragon and his angels fought back. But he was not strong enough, and they lost their place in heaven. The great dragon was hurled down—that ancient serpent called the devil, or Satan, who leads the whole world astray. He was hurled to the earth, and his angels with him."),
                Verse("Genesis 3:15", "And I will put enmity between you and the woman, and between your offspring and hers; he will crush your head, and you will strike his heel."),
                Verse("Isaiah 14:12-14", "How you have fallen from heaven, morning star, son of the dawn! You have been cast down to the earth, you who once laid low the nations! You said in your heart, 'I will ascend to the heavens; I will raise my throne above the stars of God; I will sit enthroned on the mount of assembly, on the utmost heights of Mount Zaphon. I will ascend above the tops of the clouds; I will make myself like the Most High.'"),
                Verse("Ezekiel 28:12-17", "You were the seal of perfection, full of wisdom and perfect in beauty. You were in Eden, the garden of God; every precious stone adorned you: carnelian, chrysolite and emerald, topaz, onyx and jasper, lapis lazuli, turquoise and beryl. Your settings and mountings were made of gold; on the day you were created they were prepared. You were anointed as a guardian cherub, for so I ordained you. You were on the holy mount of God; you walked among the fiery stones. You were blameless in your ways from the day you were created till wickedness was found in you."),
                Verse("Romans 1:19-20", "For what may be known about God is plain to them, because God has made it plain to them. For since the creation of the world God's invisible qualities—his eternal power and divine nature—have been clearly seen, being understood from what has been made, so that people are without excuse.")
            )),
        
        Belief(id = 9, title = "The Life, Death, and Resurrection of Christ",
            summary = "In Christ's life of perfect obedience to God's will, His suffering, death, and resurrection, God provided the only means of atonement for human sin.",
            verses = listOf(
                Verse("John 3:16", "For God so loved the world that he gave his one and only Son, that whoever believes in him shall not perish but have eternal life."),
                Verse("Isaiah 53:4-6", "Surely he took up our pain and bore our suffering, yet we considered him punished by God, stricken by him, and afflicted. But he was pierced for our transgressions, he was crushed for our iniquities; the punishment that brought us peace was on him, and by his wounds we are healed. We all, like sheep, have gone astray, each of us has turned to our own way; and the LORD has laid on him the iniquity of us all."),
                Verse("1 Peter 2:21-24", "To this you were called, because Christ suffered for you, leaving you an example, that you should follow in his steps. 'He committed no sin, and no deceit was found in his mouth.' When they hurled their insults at him, he did not retaliate; when he suffered, he made no threats. Instead, he entrusted himself to him who judges justly. 'He himself bore our sins' in his body on the cross, so that we might die to sins and live for righteousness; 'by his wounds you have been healed.'"),
                Verse("1 Corinthians 15:3-4", "For what I received I passed on to you as of first importance: that Christ died for our sins according to the Scriptures, that he was buried, that he was raised on the third day according to the Scriptures."),
                Verse("Hebrews 2:9", "But we do see Jesus, who was made lower than the angels for a little while, now crowned with glory and honor because he suffered death, so that by the grace of God he might taste death for everyone.")
            )),
        
        Belief(id = 10, title = "The Experience of Salvation",
            summary = "In infinite love and mercy God made Christ, who knew no sin, to be sin for us, so that in Him we might be made the righteousness of God.",
            verses = listOf(
                Verse("2 Corinthians 5:17-21", "Therefore, if anyone is in Christ, the new creation has come: The old has gone, the new is here! All this is from God, who reconciled us to himself through Christ and gave us the ministry of reconciliation: that God was reconciling the world to himself in Christ, not counting people's sins against them. And he has committed to us the message of reconciliation. We are therefore Christ's ambassadors, as though God were making his appeal through us. We implore you on Christ's behalf: Be reconciled to God. God made him who had no sin to be sin for us, so that in him we might become the righteousness of God."),
                Verse("John 3:3-7", "Jesus replied, \"Very truly I tell you, no one can see the kingdom of God unless they are born again.\" \"How can someone be born when they are old?\" Nicodemus asked. \"Surely they cannot enter a second time into their mother's womb to be born!\" Jesus answered, \"Very truly I tell you, no one can enter the kingdom of God unless they are born of water and the Spirit. Flesh gives birth to flesh, but the Spirit gives birth to spirit. You should not be surprised at my saying, 'You must be born again.'\""),
                Verse("1 John 5:11-12", "And this is the testimony: God has given us eternal life, and this life is in his Son. Whoever has the Son has life; whoever does not have the Son of God does not have life."),
                Verse("Ephesians 2:8-9", "For it is by grace you have been saved, through faith—and this is not from yourselves, it is the gift of God— not by works, so that no one can boast."),
                Verse("Romans 3:23-24", "For all have sinned and fall short of the glory of God, and all are justified freely by his grace through the redemption that came by Christ Jesus.")
            )),
        
        Belief(id = 11, title = "Growing in Christ",
            summary = "By His death on the cross Jesus triumphed over the forces of evil. He who subjugated the demonic spirits during His earthly ministry has broken their power and made certain their ultimate doom.",
            verses = listOf(
                Verse("John 16:8-11", "When he comes, he will prove the world to be in the wrong about sin and righteousness and judgment: about sin, because people do not believe in me; about righteousness, because I am going to the Father, where you can see me no longer; and about judgment, because the prince of this world now stands condemned."),
                Verse("Ephesians 6:12-18", "For our struggle is not against flesh and blood, but against the rulers, against the authorities, against the powers of this dark world and against the spiritual forces of evil in the heavenly realms. Therefore put on the full armor of God, so that when the day of evil comes, you may be able to stand your ground, and after you have done everything, to stand. Stand firm then, with the belt of truth buckled around your waist, with the breastplate of righteousness in place, and with your feet fitted with the readiness that comes from the gospel of peace. In addition to all this, take up the shield of faith, with which you can extinguish all the flaming arrows of the evil one. Take the helmet of salvation and the sword of the Spirit, which is the word of God. And pray in the Spirit on all occasions with all kinds of prayers and requests. With this in mind, be alert and always keep on praying for all the Lord's people."),
                Verse("1 Thessalonians 5:23", "May God himself, the God of peace, sanctify you through and through. May your whole spirit, soul and body be kept blameless at the coming of our Lord Jesus Christ."),
                Verse("2 Peter 2:9", "The Lord knows how to rescue the godly from trials and to hold the unrighteous for punishment on the day of judgment."),
                Verse("2 Corinthians 3:17-18", "Now the Lord is the Spirit, and where the Spirit of the Lord is, there is freedom. And we all, who with unveiled faces contemplate the Lord's glory, are being transformed into his image with ever-increasing glory, which comes from the Lord, who is the Spirit.")
            )),
        
        Belief(id = 12, title = "The Church",
            summary = "The church is the community of believers who confess Jesus Christ as Lord and Saviour.",
            verses = listOf(
                Verse("Ephesians 1:22-23", "And God placed all things under his feet and appointed him to be head over everything for the church, which is his body, the fullness of him who fills everything in every way."),
                Verse("1 Corinthians 12:13-14", "For we were all baptized by one Spirit so as to form one body—whether Jews or Gentiles, slave or free—and we were all given the one Spirit to drink. Even so the body is not made up of one part but of many."),
                Verse("Matthew 16:18", "And I tell you that you are Peter, and on this rock I will build my church, and the gates of Hades will not overcome it."),
                Verse("Ephesians 4:11-16", "So Christ himself gave the apostles, the prophets, the evangelists, the pastors and teachers, to equip his people for works of service, so that the body of Christ may be built up until we all reach unity in the faith and in the knowledge of the Son of God and become mature, attaining to the whole measure of the fullness of Christ."),
                Verse("Acts 2:42-47", "They devoted themselves to the apostles' teaching and to fellowship, to the breaking of bread and to prayer. Everyone was filled with awe at the many wonders and signs performed by the apostles. All the believers were together and had everything in common. They sold property and possessions to give to anyone who had need. Every day they continued to meet together in the temple courts. They broke bread in their homes and ate together with glad and sincere hearts, praising God and enjoying the favor of all the people. And the Lord added to their number daily those who were being saved.")
            )),
        
        Belief(id = 13, title = "The Remnant and Its Mission",
            summary = "The universal church is composed of all who truly believe in Christ, but in the last days, a time of widespread apostasy, a remnant has been called out to keep the commandments of God and the faith of Jesus.",
            verses = listOf(
                Verse("Revelation 12:17", "Then the dragon was enraged at the woman and went off to wage war against the rest of her offspring—those who keep God's commands and hold fast their testimony about Jesus."),
                Verse("Revelation 14:6-12", "Then I saw another angel flying in midair, and he had the eternal gospel to proclaim to those who live on the earth—to every nation, tribe, language and people. He said in a loud voice, 'Fear God and give him glory, because the hour of his judgment has come. Worship him who made the heavens, the earth, the sea and the springs of water.' A second angel followed and said, 'Fallen! Fallen is Babylon the Great, which made all the nations drink the maddening wine of her adulteries.' A third angel followed them and said in a loud voice: 'If anyone worships the beast and its image and receives its mark on their forehead or on their hand, he, too, will drink of the wine of God's fury, which has been poured full strength into the cup of his wrath. He will be tormented with burning sulfur in the presence of the holy angels and of the Lamb. And the smoke of their torment will rise for ever and ever. There will be no rest day or night for those who worship the beast and its image, or for anyone who receives the mark of its name.' This calls for patient endurance on the part of the people of God who keep his commands and remain faithful to Jesus."),
                Verse("2 Corinthians 5:10", "For we must all appear before the judgment seat of Christ, so that each of us may receive what is due us for the things done while in the body, whether good or bad."),
                Verse("Jude 3-4", "Dear friends, although I was very eager to write to you about the salvation we share, I felt compelled to write and urge you to contend for the faith that was once for all entrusted to God's holy people. For certain individuals whose condemnation was written about long ago have secretly slipped in among you. They are ungodly people, who pervert the grace of our God into a license for immorality and deny Jesus Christ our only Sovereign and Lord."),
                Verse("1 Peter 1:16-19", "For it is written: 'Be holy, because I am holy.' Since you call on a Father who judges each person's work impartially, live out your time as foreigners here in reverent fear. For you know that it was not with perishable things such as silver or gold that you were redeemed from the empty way of life handed down to you from your ancestors, but with the precious blood of Christ, a lamb without blemish or defect.")
            )),
        
        Belief(id = 14, title = "Unity in the Body of Christ",
            summary = "The church is one body with many members, called from every nation, kindred, tongue, and people.",
            verses = listOf(
                Verse("1 Corinthians 12:12-27", "Just as a body, though one, has many parts, but all its many parts form one body, so it is with Christ. For we were all baptized by one Spirit so as to form one body—whether Jews or Gentiles, slave or free—and we were all given the one Spirit to drink. Even so the body is not made up of one part but of many. Now if the foot should say, 'Because I am not a hand, I do not belong to the body,' it would not for that reason stop being part of the body. And if the ear should say, 'Because I am not an eye, I do not belong to the body,' it would not for that reason stop being part of the body. If the whole body were an eye, where would the sense of hearing be? If the whole body were an ear, where would the sense of smell be? But in fact God has placed the parts in the body, every one of them, just as he wanted them to be. If they were all one part, where would the body be? As it is, there are many parts, but one body. The eye cannot say to the hand, 'I don't need you!' And the head cannot say to the feet, 'I don't need you!' On the contrary, those parts of the body that seem to be weaker are indispensable, and the parts that we think are less honorable we treat with special honor. And the parts that are unpresentable are treated with special modesty, while our presentable parts need no special treatment. But God has put the body together, giving greater honor to the parts that lacked it, so that there should be no division in the body, but that its parts should have equal concern for each other. If one part suffers, every part suffers with it; if one part is honored, every part rejoices with it. Now you are the body of Christ, and each one of you is a part of it."),
                Verse("John 17:20-23", "My prayer is not for them alone. I pray also for those who will believe in me through their message, that all of them may be one, Father, just as you are in me and I am in you. May they also be in us so that the world may believe that you have sent me. I have given them the glory that you gave me, that they may be one as we are one— I in them and you in me—so that they may be brought to complete unity. Then the world will know that you sent me and have loved them even as you have loved me."),
                Verse("Ephesians 4:3-6", "Make every effort to keep the unity of the Spirit through the bond of peace. There is one body and one Spirit, just as you were called to one hope when you were called; one Lord, one faith, one baptism; one God and Father of all, who is over all and through all and in all."),
                Verse("Romans 12:4-5", "For just as each of us has one body with many members, and these members do not all have the same function, so in Christ we, though many, form one body, and each member belongs to all the others."),
                Verse("Galatians 3:27-28", "For all of you who were baptized into Christ have clothed yourselves with Christ. There is neither Jew nor Gentile, neither slave nor free, nor is there male and female, for you are all one in Christ Jesus.")
            )),
        
        Belief(id = 15, title = "Baptism",
            summary = "By baptism we confess our faith in the death and resurrection of Jesus Christ, and testify of our death to sin and of our purpose to walk in newness of life.",
            verses = listOf(
                Verse("Romans 6:1-6", "What shall we say, then? Shall we go on sinning so that grace may increase? By no means! We are those who have died to sin; how can we live in it any longer? Or don't you know that all of us who were baptized into Christ Jesus were baptized into his death? We were therefore buried with him through baptism into death in order that, just as Christ was raised from the dead through the glory of the Father, we too may live a new life. For if we have been united with him in a death like his, we will certainly also be united with him in a resurrection like his. For we know that our old self was crucified with him so that the body ruled by sin might be done away with, that we should no longer be slaves to sin."),
                Verse("Colossians 2:12-13", "Having been buried with him in baptism, in which you were also raised with him through your faith in the working of God, who raised him from the dead. When you were dead in your sins and in the uncircumcision of your flesh, God made you alive with Christ. He forgave us all our sins."),
                Verse("Acts 16:30-33", "He then brought them out and asked, 'Sirs, what must I do to be saved?' They replied, 'Believe in the Lord Jesus, and you will be saved—you and your household.' Then they spoke the word of the Lord to him and to all the others in his house. At that hour of the night the jailer took them and washed their wounds; then immediately he and all his household were baptized."),
                Verse("Matthew 28:19-20", "Therefore go and make disciples of all nations, baptizing them in the name of the Father and of the Son and of the Holy Spirit, and teaching them to obey everything I have commanded you. And surely I am with you always, to the very end of the age."),
                Verse("Acts 2:38", "Peter replied, 'Repent and be baptized, every one of you, in the name of Jesus Christ for the forgiveness of your sins. And you will receive the gift of the Holy Spirit.'")
            )),
        
        Belief(id = 16, title = "The Lord's Supper",
            summary = "The Lord's Supper is a participation in the emblems of the body and blood of Jesus as an expression of faith in Him, our Lord and Savior.",
            verses = listOf(
                Verse("1 Corinthians 10:16-17", "Is not the cup of thanksgiving for which we give thanks a participation in the blood of Christ? And is not the bread that we break a participation in the body of Christ? Because there is one loaf, we, who are many, are one body, for we all share the one loaf."),
                Verse("1 Corinthians 11:23-26", "For I received from the Lord what I also passed on to you: The Lord Jesus, on the night he was betrayed, took bread, and when he had given thanks, he broke it and said, \"This is my body, which is for you; do this in remembrance of me.\" In the same way, after supper he took the cup, saying, \"This cup is the new covenant in my blood; do this, whenever you drink it, in remembrance of me.\" For whenever you eat this bread and drink this cup, you proclaim the Lord's death until he comes."),
                Verse("Matthew 26:17-30", "On the first day of the Festival of Unleavened Bread, the disciples came to Jesus and asked, \"Where do you want us to make preparations for you to eat the Passover?\" He replied, \"Go into the city to a certain man and tell him, 'The Teacher says: My appointed time is near. I am going to celebrate the Passover with my disciples at your house.'\" So the disciples did as Jesus had directed them and prepared the Passover. When evening came, Jesus was reclining at the table with the Twelve. And while they were eating, he said, \"Truly I tell you, one of you will betray me.\" They were very sad and began to say to him one after the other, \"Surely you don't mean me, Lord?\" Jesus replied, \"The one who has dipped his hand into the bowl with me will betray me. The Son of Man will go just as it is written about him. But woe to that man who betrays the Son of Man! It would be better for him if he had not been born.\" Then Judas, the one who would betray him, said, \"Surely you don't mean me, Rabbi?\" Jesus answered, \"You have said so.\" While they were eating, Jesus took bread, and when he had given thanks, he broke it and gave it to his disciples, saying, \"Take and eat; this is my body.\" Then he took a cup, and when he had given thanks, he gave it to them, saying, \"Drink from it, all of you. This is my blood of the covenant, which is poured out for many for the forgiveness of sins. I tell you, I will not drink from this fruit of the vine from now on until that day when I drink it new with you in my Father's kingdom.\" When they had sung a hymn, they went out to the Mount of Olives."),
                Verse("John 6:48-58", "I am the bread of life. Your ancestors ate the manna in the wilderness, yet they died. But here is the bread that comes down from heaven, which anyone may eat and not die. I am the living bread that came down from heaven. Whoever eats this bread will live forever. This bread is my flesh, which I will give for the life of the world.\" Then the Jews began to argue sharply among themselves, \"How can this man give us his flesh to eat?\" Jesus said to them, \"Very truly I tell you, unless you eat the flesh of the Son of Man and drink his blood, you have no life in you. Whoever eats my flesh and drinks my blood has eternal life, and I will raise them up at the last day. For my flesh is real food and my blood is real drink. Whoever eats my flesh and drinks my blood remains in me, and I in them. Just as the living Father sent me and I live because of the Father, so the one who feeds on me will live because of me. This is the bread that came down from heaven. Your ancestors ate manna and died, but whoever feeds on this bread will live forever.\""),
                Verse("Revelation 3:20", "Here I am! I stand at the door and knock. If anyone hears my voice and opens the door, I will come in and eat with that person, and they with me.")
            )),
        
        Belief(id = 17, title = "Spiritual Gifts and Ministries",
            summary = "God bestows upon all members of His church spiritual gifts which each member is to employ in loving ministry for the common good of the church and humanity.",
            verses = listOf(
                Verse("1 Corinthians 12:4-11", "There are different kinds of gifts, but the same Spirit distributes them. There are different kinds of service, but the same Lord. There are different kinds of working, but in all of them and in everyone it is the same God at work. Now to each one the manifestation of the Spirit is given for the common good. To one there is given through the Spirit a message of wisdom, to another a message of knowledge by means of the same Spirit, to another faith by the same Spirit, to another gifts of healing by that one Spirit, to another miraculous powers, to another prophecy, to another distinguishing between spirits, to another speaking in different kinds of tongues, and to still another the interpretation of tongues. All these are the work of one and the same Spirit, and he distributes them to each one, just as he determines."),
                Verse("Ephesians 4:11-13", "So Christ himself gave the apostles, the prophets, the evangelists, the pastors and teachers, to equip his people for works of service, so that the body of Christ may be built up until we all reach unity in the faith and in the knowledge of the Son of God and become mature, attaining to the whole measure of the fullness of Christ."),
                Verse("Romans 12:6-8", "We have different gifts, according to the grace given to each of us. If your gift is prophesying, then prophesy in accordance with your faith; if it is serving, then serve; if it is teaching, then teach; if it is to encourage, then give encouragement; if it is giving, then give generously; if it is to lead, do it diligently; if it is to show mercy, do it cheerfully."),
                Verse("1 Peter 4:10-11", "Each of you should use whatever gift you have received to serve others, as faithful stewards of God's grace in its various forms. If anyone speaks, they should do so as one who speaks the very words of God. If anyone serves, they should do so with the strength God provides, so that in all things God may be praised through Jesus Christ. To him be the glory and the power for ever and ever. Amen."),
                Verse("Joel 2:28-29", "And afterward, I will pour out my Spirit on all people. Your sons and daughters will prophesy, your old men will dream dreams, your young men will see visions. Even on my servants, both men and women, I will pour out my Spirit in those days.")
            )),
        
        Belief(id = 18, title = "The Gift of Prophecy",
            summary = "The Scriptures testify that one of the gifts of the Holy Spirit is prophecy. This gift is an identifying mark of the remnant church and we believe it was manifested in the ministry of Ellen G. White.",
            verses = listOf(
                Verse("Joel 2:28-29", "And afterward, I will pour out my Spirit on all people. Your sons and daughters will prophesy, your old men will dream dreams, your young men will see visions. Even on my servants, both men and women, I will pour out my Spirit in those days."),
                Verse("Acts 2:14-21", "Then Peter stood up with the Eleven, raised his voice and addressed the crowd: 'Fellow Jews and all of you who live in Jerusalem, let me explain this to you; listen carefully to what I say. These people are not drunk, as you suppose. It's only nine in the morning! No, this is what was spoken by the prophet Joel: 'In the last days, God says, I will pour out my Spirit on all people. Your sons and daughters will prophesy, your young men will see visions, your old men will dream dreams. Even on my servants, both men and women, I will pour out my Spirit in those days, and they will prophesy. I will show wonders in the heavens above and signs on the earth below, blood and fire and billows of smoke. The sun will be turned to darkness and the moon to blood before the coming of the great and glorious day of the Lord. And everyone who calls on the name of the Lord will be saved.'"),
                Verse("Numbers 12:6", "He said, 'Listen to my words: When there is a prophet among you, I, the LORD, reveal myself to them in visions, I speak to them in dreams.'"),
                Verse("Amos 3:7", "Surely the Sovereign LORD does nothing without revealing his plan to his servants the prophets."),
                Verse("1 Corinthians 14:1", "Follow the way of love and eagerly desire gifts of the Spirit, especially prophecy.")
            )),
        
        Belief(id = 19, title = "The Law of God",
            summary = "The great principles of God's law are embodied in the Ten Commandments and exemplified in the life of Christ. They express God's love, will, and purposes.",
            verses = listOf(
                Verse("Exodus 20:1-17", "And God spoke all these words: 'I am the LORD your God, who brought you out of Egypt, out of the land of slavery. You shall have no other gods before me. You shall not make for yourself an image in the form of anything in heaven above or on the earth beneath or in the waters below. You shall not bow down to them or worship them; for I, the LORD your God, am a jealous God, punishing the children for the sin of the parents to the third and fourth generation of those who hate me, but showing love to a thousand generations of those who love me and keep my commandments. You shall not misuse the name of the LORD your God, for the LORD will not hold anyone guiltless who misuses his name. Remember the Sabbath day by keeping it holy. Six days you shall labor and do all your work, but the seventh day is a sabbath to the LORD your God. On it you shall not do any work, neither you, nor your son or daughter, nor your male or female servant, nor your animals, nor any foreigner residing in your towns. For in six days the LORD made the heavens and the earth, the sea, and all that is in them, but he rested on the seventh day. Therefore the LORD blessed the Sabbath day and made it holy. Honor your father and your mother, so that you may live long in the land the LORD your God is giving you. You shall not murder. You shall not commit adultery. You shall not steal. You shall not give false testimony against your neighbor. You shall not covet your neighbor's house. You shall not covet your neighbor's wife, or his male or female servant, his ox or donkey, or anything that belongs to your neighbor.'"),
                Verse("Psalm 40:8", "I desire to do your will, my God; your law is within my heart."),
                Verse("Matthew 5:17-20", "Do not think that I have come to abolish the Law or the Prophets; I have not come to abolish them but to fulfill them. For truly I tell you, until heaven and earth disappear, not the smallest letter, not the least stroke of a pen, will by any means disappear from the Law until everything is accomplished. Therefore anyone who sets aside one of the least of these commands and teaches others accordingly will be called least in the kingdom of heaven, but whoever practices and teaches these commands will be called great in the kingdom of heaven. For I tell you that unless your righteousness surpasses that of the Pharisees and the teachers of the law, you will certainly not enter the kingdom of heaven."),
                Verse("Romans 8:3-4", "For what the law was powerless to do because it was weakened by the flesh, God did by sending his own Son in the likeness of sinful flesh to be a sin offering. And so he condemned sin in the flesh, in order that the righteous requirement of the law might be fully met in us, who do not live according to the flesh but according to the Spirit."),
                Verse("John 15:10", "If you keep my commands, you will remain in my love, just as I have kept my Father's commands and remain in his love.")
            )),
        
        Belief(id = 20, title = "The Sabbath",
            summary = "The gracious Creator, after the six days of Creation, rested on the seventh day and instituted the Sabbath for all people as a memorial of Creation.",
            verses = listOf(
                Verse("Genesis 2:1-3", "Thus the heavens and the earth were completed in all their vast array. By the seventh day God had finished the work he had been doing; so on the seventh day he rested from all his work. Then God blessed the seventh day and made it holy, because on it he rested from all the work of creating that he had done."),
                Verse("Exodus 20:8-11", "Remember the Sabbath day by keeping it holy. Six days you shall labor and do all your work, but the seventh day is a sabbath to the LORD your God. On it you shall not do any work, neither you, nor your son or daughter, nor your male or female servant, nor your animals, nor any foreigner residing in your towns. For in six days the LORD made the heavens and the earth, the sea, and all that is in them, but he rested on the seventh day. Therefore the LORD blessed the Sabbath day and made it holy."),
                Verse("Mark 2:27-28", "Then he said to them, 'The Sabbath was made for man, not man for the Sabbath. So the Son of Man is Lord even of the Sabbath.'"),
                Verse("Isaiah 58:13-14", "If you keep your feet from breaking the Sabbath and from doing as you please on my holy day, if you call the Sabbath a delight and the LORD's holy day honorable, and if you honor it by not going your own way and not doing as you please or speaking idle words, then you will find your joy in the LORD, and I will cause you to ride in triumph on the heights of the land and to feast on the inheritance of your father Jacob.' The mouth of the LORD has spoken."),
                Verse("Luke 4:16", "He went to Nazareth, where he had been brought up, and on the Sabbath day he went into the synagogue, as was his custom.")
            )),
        
        Belief(id = 21, title = "Stewardship",
            summary = "We are God's stewards, entrusted by Him with time and opportunities, abilities and possessions, and the blessings of the earth and its resources.",
            verses = listOf(
                Verse("Genesis 1:26-28", "Then God said, 'Let us make mankind in our image, in our likeness, so that they may rule over the fish in the sea and the birds in the sky, over the livestock and all the wild animals, and over all the creatures that move along the ground.' So God created mankind in his own image, in the image of God he created them; male and female he created them. God blessed them and said to them, 'Be fruitful and increase in number; fill the earth and subdue it. Rule over the fish in the sea and the birds in the sky and over every living creature that moves on the ground.'"),
                Verse("Psalm 24:1", "The earth is the LORD's, and everything in it, the world, and all who live in it."),
                Verse("Leviticus 27:30", "A tithe of everything from the land, whether grain from the soil or fruit from the trees, belongs to the LORD; it is holy to the LORD."),
                Verse("Malachi 3:8-12", "Will a mere mortal rob God? Yet you rob me. But you ask, 'How are we robbing you?' In tithes and offerings. You are under a curse—your whole nation—because you are robbing me. Bring the whole tithe into the storehouse, that there may be food in my house. Test me in this,' says the LORD Almighty. 'And see if I will not throw open the floodgates of heaven and pour out so much blessing that there will not be room enough to store it. I will prevent pests from devouring your crops, and the vines in your fields will not drop their fruit before it is ripe,' says the LORD Almighty. 'Then all the nations will call you blessed, for yours will be a delightful land,' says the LORD Almighty."),
                Verse("Matthew 25:14-30", "Again, it will be like a man going on a journey, who called his servants and entrusted his wealth to them. To one he gave five bags of gold, to another two bags, and to another one bag, each according to his ability. Then he went on his journey. The man who had received five bags of gold went at once and put his money to work and gained five bags more. So also, the one with two bags of gold gained two more. But the man who had received one bag went off, dug a hole in the ground and hid his master's money. After a long time the master of those servants returned and settled accounts with them. The man who had received five bags of gold brought the other five. 'Master,' he said, 'you entrusted me with five bags of gold. See, I have gained five more.' His master replied, 'Well done, good and faithful servant! You have been faithful with a few things; I will put you in charge of many things. Come and share your master's happiness!' Then the man who had received two bags of gold also came. 'Master,' he said, 'you entrusted me with two bags of gold; see, I have gained two more.' His master replied, 'Well done, good and faithful servant! You have been faithful with a few things; I will put you in charge of many things. Come and share your master's happiness!' Then the man who had received one bag of gold came. 'Master,' he said, 'I knew that you are a hard man, harvesting where you have not sown and gathering where you have not scattered seed. So I was afraid and went out and hid your gold in the ground. See, here is what belongs to you.' His master replied, 'You wicked, lazy servant! So you knew that I harvest where I have not sown and gather where I have not scattered seed? Well then, you should have put my money on deposit with the bankers, so that when I returned I would have received it back with interest. Take the bag of gold from him and give it to the one who has ten bags. For whoever has will be given more, and they will have an abundance. Whoever does not have, even what they have will be taken from them. And throw that worthless servant outside, into the darkness, where there will be weeping and gnashing of teeth.'")
            )),
        
        Belief(id = 22, title = "Christian Behavior",
            summary = "We are called to be a godly people who think, feel, and act in harmony with the principles of heaven.",
            verses = listOf(
                Verse("1 John 2:6", "Whoever claims to live in him must live as Jesus did."),
                Verse("Ephesians 5:1-21", "Follow God's example, therefore, as dearly loved children and walk in the way of love, just as Christ loved us and gave himself up for us as a fragrant offering and sacrifice to God. But among you there must not be even a hint of sexual immorality, or of any kind of impurity, or of greed, because these are improper for God's holy people. Nor should there be obscenity, foolish talk or coarse joking, which are out of place, but rather thanksgiving. For of this you can be sure: No immoral, impure or greedy person—such a person is an idolater—has any inheritance in the kingdom of Christ and of God. Let no one deceive you with empty words, for because of such things God's wrath comes on those who are disobedient. Therefore do not be partners with them. For you were once darkness, but now you are light in the Lord. Live as children of light (for the fruit of the light consists in all goodness, righteousness and truth) and find out what pleases the Lord. Have nothing to do with the fruitless deeds of darkness, but rather expose them. It is shameful even to mention what the disobedient do in secret. But everything exposed by the light becomes visible—and everything that is illuminated becomes a light. This is why it is said: 'Wake up, sleeper, rise from the dead, and Christ will shine on you.' Be very careful, then, how you live—not as unwise but as wise, making the most of every opportunity, because the days are evil. Therefore do not be foolish, but understand what the Lord's will is. Do not get drunk on wine, which leads to debauchery. Instead, be filled with the Spirit, speaking to one another with psalms, hymns, and songs from the Spirit. Sing and make music from your heart to the Lord, always giving thanks to God the Father for everything, in the name of our Lord Jesus Christ. Submit to one another out of reverence for Christ."),
                Verse("Romans 12:1-2", "Therefore, I urge you, brothers and sisters, in view of God's mercy, to offer your bodies as a living sacrifice, holy and pleasing to God—this is your true and proper worship. Do not conform to the pattern of this world, but be transformed by the renewing of your mind. Then you will be able to test and approve what God's will is—his good, pleasing and perfect will."),
                Verse("1 Corinthians 6:19-20", "Do you not know that your bodies are temples of the Holy Spirit, who is in you, whom you have received from God? You are not your own; you were bought at a price. Therefore honor God with your bodies."),
                Verse("1 Corinthians 10:31", "So whether you eat or drink or whatever you do, do it all for the glory of God.")
            )),
        
        Belief(id = 23, title = "Marriage and the Family",
            summary = "Marriage was divinely established in Eden and affirmed by Jesus to be a lifelong union between a man and a woman in loving companionship.",
            verses = listOf(
                Verse("Genesis 2:18-25", "The LORD God said, 'It is not good for the man to be alone. I will make a helper suitable for him.' Now the LORD God had formed out of the ground all the wild animals and all the birds in the sky. He brought them to the man to see what he would name them; and whatever the man called each living creature, that was its name. So the man gave names to all the livestock, the birds in the sky and all the wild animals. But for Adam no suitable helper was found. So the LORD God caused the man to fall into a deep sleep; and while he was sleeping, he took one of the man's ribs and then closed up the place with flesh. Then the LORD God made a woman from the rib he had taken out of the man, and he brought her to the man. The man said, 'This is now bone of my bones and flesh of my flesh; she shall be called 'woman,' for she was taken out of man.' That is why a man leaves his father and mother and is united to his wife, and they become one flesh. Adam and his wife were both naked, and they felt no shame."),
                Verse("Matthew 19:3-9", "Some Pharisees came to him to test him. They asked, 'Is it lawful for a man to divorce his wife for any and every reason?' 'Haven't you read,' he replied, 'that at the beginning the Creator 'made them male and female,' and said, 'For this reason a man will leave his father and mother and be united to his wife, and the two will become one flesh'? So they are no longer two, but one flesh. Therefore what God has joined together, let no one separate.' 'Why then,' they asked, 'did Moses command that a man give his wife a certificate of divorce and send her away?' Jesus replied, 'Moses permitted you to divorce your wives because your hearts were hard. But it was not this way from the beginning. I tell you that anyone who divorces his wife, except for sexual immorality, and marries another woman commits adultery.'"),
                Verse("Ephesians 5:21-33", "Submit to one another out of reverence for Christ. Wives, submit yourselves to your own husbands as you do to the Lord. For the husband is the head of the wife as Christ is the head of the church, his body, of which he is the Savior. Now as the church submits to Christ, so also wives should submit to their husbands in everything. Husbands, love your wives, just as Christ loved the church and gave himself up for her to make her holy, cleansing her by the washing with water through the word, and to present her to himself as a radiant church, without stain or wrinkle or any other blemish, but holy and blameless. In this same way, husbands ought to love their wives as their own bodies. He who loves his wife loves himself. After all, no one ever hated their own body, but they feed and care for their body, just as Christ does the church— for we are members of his body. For this reason a man will leave his father and mother and be united to his wife, and the two will become one flesh. This is a profound mystery—but I am talking about Christ and the church. However, each one of you also must love his wife as he loves himself, and the wife must respect her husband."),
                Verse("Proverbs 22:6", "Start children off on the way they should go, and even when they are old they will not turn from it."),
                Verse("Malachi 2:14-16", "You ask, 'Why?' It is because the LORD is the witness between you and the wife of your youth. You have been unfaithful to her, though she is your partner, the wife of your marriage covenant. Has not the one God made you? You belong to him in body and spirit. And what does the one God seek? Godly offspring. So be on your guard, and do not be unfaithful to the wife of your youth. I hate divorce,' says the LORD God of Israel, 'and I hate a man's covering himself with violence as well as with his garment,' says the LORD Almighty. So guard yourself in your spirit, and do not be unfaithful.")
            )),
        
        Belief(id = 24, title = "Christ's Ministry in the Heavenly Sanctuary",
            summary = "There is a sanctuary in heaven, the true tabernacle which the Lord set up and not man. In it Christ ministers on our behalf.",
            verses = listOf(
                Verse("Hebrews 8:1-2", "Now the main point of what we are saying is this: We do have such a high priest, who sat down at the right hand of the throne of the Majesty in heaven, and who serves in the sanctuary, the true tabernacle set up by the Lord, not by a mere human being."),
                Verse("Hebrews 9:11-12", "But when Christ came as high priest of the good things that are now already here, he went through the greater and more perfect tabernacle that is not made with human hands, that is to say, is not a part of this creation. He did not enter by means of the blood of goats and calves; but he entered the Most Holy Place once for all by his own blood, thus obtaining eternal redemption."),
                Verse("Daniel 7:9-10", "As I looked, thrones were set in place, and the Ancient of Days took his seat. His clothing was as white as snow; the hair of his head was white like wool. His throne was flaming with fire, and its wheels were all ablaze. A river of fire was flowing, coming out from before him. Thousands upon thousands attended him; ten thousand times ten thousand stood before him. The court was seated, and the books were opened."),
                Verse("Revelation 11:19", "Then God's temple in heaven was opened, and within his temple was seen the ark of his covenant. And there came flashes of lightning, rumblings, peals of thunder, an earthquake and a severe hailstorm."),
                Verse("Hebrews 9:23-24", "It was necessary, then, for the copies of the heavenly things to be purified with these sacrifices, but the heavenly things themselves with better sacrifices than these. For Christ did not enter a sanctuary made with human hands that was only a copy of the true one; he entered heaven itself, now to appear for us in God's presence.")
            )),
        
        Belief(id = 25, title = "The Second Coming of Christ",
            summary = "The second coming of Christ is the blessed hope of the church, the grand climax of the gospel.",
            verses = listOf(
                Verse("Titus 2:13", "While we wait for the blessed hope—the appearing of the glory of our great God and Savior, Jesus Christ."),
                Verse("John 14:1-3", "Do not let your hearts be troubled. You believe in God; believe also in me. My Father's house has many rooms; if that were not so, would I have told you that I am going there to prepare a place for you? And if I go and prepare a place for you, I will come back and take you to be with me that you also may be where I am."),
                Verse("Acts 1:9-11", "After he said this, he was taken up before their very eyes, and a cloud hid him from their sight. They were looking intently up into the sky as he was going, when suddenly two men dressed in white stood beside them. 'Men of Galilee,' they said, 'why do you stand here looking into the sky? This same Jesus, who has been taken from you into heaven, will come back in the same way you have seen him go into heaven.'"),
                Verse("Matthew 24:27", "For as lightning that comes from the east is visible even in the west, so will be the coming of the Son of Man."),
                Verse("Revelation 1:7", "Look, he is coming with the clouds, and every eye will see him, even those who pierced him; and all peoples on earth will mourn because of him. So shall it be! Amen.")
            )),
        
        Belief(id = 26, title = "Death and Resurrection",
            summary = "The wages of sin is death. But God, who alone is immortal, will grant eternal life to His redeemed.",
            verses = listOf(
                Verse("Romans 6:23", "For the wages of sin is death, but the gift of God is eternal life in Christ Jesus our Lord."),
                Verse("1 Timothy 6:15-16", "Which God will bring about in his own time—God, the blessed and only Ruler, the King of kings and Lord of lords, who alone is immortal and who lives in unapproachable light, whom no one has seen or can see. To him be honor and might forever. Amen."),
                Verse("Ecclesiastes 9:5-6", "For the living know that they will die, but the dead know nothing; they have no further reward, and even their name is forgotten. Their love, their hate and their jealousy have long since vanished; never again will they have a part in anything that happens under the sun."),
                Verse("Psalm 146:4", "When their spirit departs, they return to the ground; on that very day their plans come to nothing."),
                Verse("John 5:28-29", "Do not be amazed at this, for a time is coming when all who are in their graves will hear his voice and come out—those who have done what is good will rise to live, and those who have done what is evil will rise to be condemned.")
            )),
        
        Belief(id = 27, title = "The Millennium and the End of Sin",
            summary = "The millennium is the thousand-year reign of Christ with His saints in heaven between the first and second resurrections.",
            verses = listOf(
                Verse("Revelation 20:1-6", "And I saw an angel coming down out of heaven, having the key to the Abyss and holding in his hand a great chain. He seized the dragon, that ancient serpent, who is the devil, or Satan, and bound him for a thousand years. He threw him into the Abyss, and locked and sealed it over him, to keep him from deceiving the nations anymore until the thousand years were ended. After that, he must be set free for a short time. I saw thrones on which were seated those who had been given authority to judge. And I saw the souls of those who had been beheaded because of their testimony about Jesus and because of the word of God. They had not worshiped the beast or its image and had not received its mark on their foreheads or their hands. They came to life and reigned with Christ a thousand years. (The rest of the dead did not come to life until the thousand years were ended.) This is the first resurrection. Blessed and holy are those who share in the first resurrection. The second death has no power over them, but they will be priests of God and of Christ and will reign with him for a thousand years."),
                Verse("Revelation 20:7-10", "When the thousand years are over, Satan will be released from his prison and will go out to deceive the nations in the four corners of the earth—Gog and Magog—and to gather them for battle. In number they are like the sand on the seashore. They marched across the breadth of the earth and surrounded the camp of God's people, the city he loves. But fire came down from heaven and devoured them. And the devil, who deceived them, was thrown into the lake of burning sulfur, where the beast and the false prophet had been thrown. They will be tormented day and night for ever and ever."),
                Verse("Jeremiah 4:23-26", "I looked at the earth, and it was formless and empty; and at the heavens, and their light was gone. I looked at the mountains, and they were quaking; all the hills were swaying. I looked, and there were no people; every bird in the sky had flown away. I looked, and the fruitful land was a desert; all its towns lay in ruins before the LORD, before his fierce anger."),
                Verse("Malachi 4:1", "Surely the day is coming; it will burn like a furnace. All the arrogant and every evildoer will be stubble, and the day that is coming will set them on fire,' says the LORD Almighty. 'Not a root or a branch will be left to them.'"),
                Verse("2 Peter 3:10-13", "But the day of the Lord will come like a thief. The heavens will disappear with a roar; the elements will be destroyed by fire, and the earth and everything done in it will be laid bare. Since everything will be destroyed in this way, what kind of people ought you to be? You ought to live holy and godly lives as you look forward to the day of God and speed its coming. That day will bring about the destruction of the heavens by fire, and the elements will melt in the heat. But in keeping with his promise we are looking forward to a new heaven and a new earth, where righteousness dwells.")
            )),
        
        Belief(id = 28, title = "The New Earth",
            summary = "On the new earth, in which righteousness dwells, God will provide an eternal home for the redeemed and a perfect environment for everlasting life, love, joy, and learning in His presence.",
            verses = listOf(
                Verse("2 Peter 3:13", "But in keeping with his promise we are looking forward to a new heaven and a new earth, where righteousness dwells."),
                Verse("Isaiah 35:1-10", "The desert and the parched land will be glad; the wilderness will rejoice and blossom. Like the crocus, it will burst into bloom; it will rejoice greatly and shout for joy. The glory of Lebanon will be given to it, the splendor of Carmel and Sharon; they will see the glory of the LORD, the splendor of our God. Strengthen the feeble hands, steady the knees that give way; say to those with fearful hearts, 'Be strong, do not fear; your God will come, he will come with vengeance; with divine retribution he will come to save you.' Then will the eyes of the blind be opened and the ears of the deaf unstopped. Then will the lame leap like a deer, and the mute tongue shout for joy. Water will gush forth in the wilderness and streams in the desert. The burning sand will become a pool, the thirsty ground bubbling springs. In the haunts where jackals once lay, grass and reeds and papyrus will grow. And a highway will be there; it will be called the Way of Holiness; it will be for those who walk on that Way. The unclean will not journey on it; wicked fools will not go about on it. No lion will be there, nor any ravenous beast; they will not be found there. But only the redeemed will walk there, and those the LORD has rescued will return. They will enter Zion with singing; everlasting joy will crown their heads. Gladness and joy will overtake them, and sorrow and sighing will flee away."),
                Verse("Revelation 21:1-7", "Then I saw 'a new heaven and a new earth,' for the first heaven and the first earth had passed away, and there was no longer any sea. I saw the Holy City, the new Jerusalem, coming down out of heaven from God, prepared as a bride beautifully dressed for her husband. And I heard a loud voice from the throne saying, 'Look! God's dwelling place is now among the people, and he will dwell with them. They will be his people, and God himself will be with them and be their God. 'He will wipe every tear from their eyes. There will be no more death' or mourning or crying or pain, for the old order of things has passed away.' He who was seated on the throne said, 'I am making everything new!' Then he said, 'Write this down, for these words are trustworthy and true.'"),
                Verse("Revelation 22:1-5", "Then the angel showed me the river of the water of life, as clear as crystal, flowing from the throne of God and of the Lamb down the middle of the great street of the city. On each side of the river stood the tree of life, bearing twelve crops of fruit, yielding its fruit every month. And the leaves of the tree are for the healing of the nations. No longer will there be any curse. The throne of God and of the Lamb will be in the city, and his servants will serve him. They will see his face, and his name will be on their foreheads. There will be no more night. They will not need the light of a lamp or the light of the sun, for the Lord God will give them light. And they will reign for ever and ever."),
                Verse("Matthew 5:5", "Blessed are the meek, for they will inherit the earth.")
            ))
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBeliefsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.beliefsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = BeliefsAdapter(beliefs)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private inner class BeliefsAdapter(private val beliefs: List<Belief>) : 
        RecyclerView.Adapter<BeliefsAdapter.BeliefViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeliefViewHolder {
            val binding = ItemBeliefBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return BeliefViewHolder(binding)
        }

        override fun onBindViewHolder(holder: BeliefViewHolder, position: Int) {
            holder.bind(beliefs[position])
        }

        override fun getItemCount() = beliefs.size

        inner class BeliefViewHolder(private val binding: ItemBeliefBinding) : 
            RecyclerView.ViewHolder(binding.root) {

            fun bind(belief: Belief) {
                binding.beliefNumberText.text = "${belief.id}."
                binding.beliefTitleText.text = belief.title
                binding.beliefSummaryText.text = belief.summary
                binding.versesText.text = belief.verses.joinToString("\n") { it.reference }
                
                // Make verses clickable
                binding.versesText.setOnClickListener { view ->
                    val verses = belief.verses
                    val verseReferences = verses.map { it.reference }.toTypedArray()
                    
                    MaterialAlertDialogBuilder(view.context)
                        .setTitle("Select a Verse")
                        .setItems(verseReferences) { _, which ->
                            val selectedVerse = verses[which]
                            showVerseDialog(selectedVerse, belief)
                        }
                        .show()
                }
            }
        }
    }

    private fun showVerseDialog(verse: Verse, belief: Belief) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(verse.reference)
            .setMessage(verse.content)
            .setPositiveButton("Back to Verses") { dialog, _ ->
                dialog.dismiss()
                // Show the verse selection dialog again
                val verses = belief.verses
                val verseReferences = verses.map { it.reference }.toTypedArray()
                
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Select a Verse")
                    .setItems(verseReferences) { _, which ->
                        val selectedVerse = verses[which]
                        showVerseDialog(selectedVerse, belief)
                    }
                    .show()
            }
            .setNegativeButton("Close") { dialog, _ ->
                dialog.dismiss()
            }
            .setBackgroundInsetStart(32)
            .setBackgroundInsetEnd(32)
            .show()
            .apply {
                getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)?.setTextColor(requireContext().getColor(android.R.color.holo_blue_dark))
                getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE)?.setTextColor(requireContext().getColor(android.R.color.holo_red_dark))
            }
    }
} 