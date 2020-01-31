Mailinglist Moderator
=====================

Dies ist nur ein kleines Programm, um das Moderieren von Mailinglisten von unterwegs aus ein wenig zu erleichtern. 

Das Programm zeigt alle ausstehenden Moderationsanfragen an und ermöglichen es diese zu genehmigen, abzulehnen, zu verwerfen* oder als Spam (Listenübergreifend)* zu markieren. Bei einer Ablehnung kann zudem eine Ablehnungsnachricht spezifiziert werden.*

Zu diesem Zeitpunkt können nur Beiträge moderiert werden und keine Anmeldungen für eine Mailingliste.

(* Neu in dieser Weiterentwicklung)



Einrichten einer Liste
-----------------
To add a new mailinglist, hit the *Menu* key and select *Servers...*. Hit
*Menu* again, and select *New server*. In the window that shows up, enter the
*name of the mailinglist*. (Yes, there is some confusion of terms here, but this
is the name of the list, not the server).

When you've done this, you will be sent back to the main screen, and your list
will show up as unconfigured (in a future improved version, you should end up
directly at the list configuration page here). To configure the details, hit
the *Menu* key, and select *Servers* (again). Now click on the new server to
go into the configuration page for it, where you can enter the base URL and
the password.

If you have one or more existing servers, you can easily duplicate them by
clicking and holding on the name of the server and choose copy. This will copy
the base url and password, but give you a chance to enter a new name for the
list. This is particularly useful if you have a "site-wide" administration
password that will grant you access to all lists.

Base URLs
~~~~~~~~~
The *base url* should be set to the root of the list server management URL.
It will be different depending on which list manager is used. Note that the
base url does *not* include the name of the list.

Mailman
+++++++
For mailman, the base url is typically ``http://lists.domain.com/mailman/admindb``.



Importing and exporting
~~~~~~~~~~~~~~~~~~~~~~~
You can import and export your list of servers to a XML file from the menu. This
file will be placed in the `/sdcard/` folder, so you can use any program to edit
or transfer it. Note that there is no real validation done when importing, so if
the XML file has an invalid format or content, the program is likely to just
crash instead of giving an error message.

SSL
~~~
In it's default mode, Mailinglist Moderator will rely on the Android system to
validate the certificate of the server connected to, if the URL starts with
`https`. There are two settings to override this (per server), if you are using
a certificate that doesn't validate properly:

Non-standard SSL hostname
    This setting is used if the certificate has a different hostname than the
    URL, but otherwise validates fine. A typical example of this is overloading
    hostnames, so the certificate is for `www.domain.com`, but the list server
    is responding to `lists.domain.com`. In this case, put `www.domain.com` in
    settings as *Non-standard SSL hostname*.
Accept invalid certificate
    This setting is used when the certificate on the server simply does not
    validate. This could be because it's a self signed certificate, because the
    CA signing it is not known, because it has expired, or any other reasons.
    The configuration is set to the *SHA-1 fingerprint* of the certificate, and
    any server that presents this fingerprint will be accepted. When you enter
    this setting, the current certificate will automatically be downloaded and
    presented, and you can press a button to copy the fingerprint to the setting,
    once you have verified that it's correct. *Note* that you will have to
    set the *Non-standard SSL hostname* setting as well as this one, in case the
    name on the certificate doesn't match.

Moderating
----------
When the application starts it will enumerate all unmoderated emails on all the
lists, and the main view will show how many unmoderated messages there are on the
list. The entries will be sorted so that the list with the largest amount of
pending moderation requests is listed on top.

To moderate the entries on a list, just click the list from the main view. This
will give a list of all the emails pending on this list, the sender of them
and the subject. To view the contents of the mail, just click the mail.

To moderate an individual email, click-hold on it and choose to reject, approve, defer it* or to mark it as spam*.
You can also hit the *Menu* key and from the popup menu choose to reject
or approve all messages at once.

Once you're satisfied with the status of the messages, hit the *Menu* key and
select *Apply moderation*. This will call out to the server and make the actual
moderation changes.


Common issues
-------------

* The mailinglist software to be in GERMAN - localized versions don't work.
