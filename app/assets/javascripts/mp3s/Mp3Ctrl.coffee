


class Mp3Ctrl
 
    constructor: (@$log, @$scope, @$rootScope, @Mp3Service) ->
        @$log.debug "constructing Mp3Controller"
        @currentTrack = 0
        @currentIndex = 0
        @pageSize = 5
        @mp3s = []
        @mp3 = []
        @mp3Data = []
        @playlst = []
        @artists = []
        @artist = []
        @albums = []
        @album = []
        @genres = []
        @genre = []
        @title = []
        @getAllMp3s()
        @getAllArtists()
        @getAllAlbums()
        @getAllGenres()
        @isDebug = false
        @shuffle = false
        @init( this)
   
 
    init: (us) ->
      
       @$scope.$on 'audio.next',() ->
         console.log('Next')
         us.next() 
     
       @$scope.$on 'audio.prev',() ->
          console.log('Prev ')
          us.prev()

       @$rootScope.$on 'audio.ended',() ->
          console.log('Ended')
          us.next() 

                 
    next: () ->        
      console.log('Next ' + @currentTrack )
      if(@shuffle== true)
         @currentIndex++
         if (@currentIndex <@mp3s.length)
           @currentTrack = @playlst[@currentIndex]
           @updateTrack();
         else
            @currentTrack= playlst[@mp3s.length-1];
      else      
        @currentTrack++;
        if (@currentTrack <@mp3s.length)
          @updateTrack();
        else
          @currentTrack= @mp3s.length-1;
     
                 
    prev: () -> 
      console.log('Prev ' + @currentTrack)
      if(@shuffle== true)
        @currentIndex++
        if (@currentIndex >= 0)
          @currentTrack = @playlst[@currentIndex]
          @updateTrack();
        else
           @currentTrack= playlst[0];
      else 
        @currentTrack--;
        if (@currentTrack >= 0)
          @updateTrack();
        else
          @currentTrack = 0;
  
     
    updateTrack: () ->
      @$log.debug "updateTrack()" 
      @$rootScope.$broadcast('audio.set', 'mp3/'+ @mp3s[@currentTrack]._id.$oid,  @mp3s[@currentTrack], @currentTrack, @mp3s.length);
      @$rootScope.$broadcast('audio.play',this) 
      for element in document.getElementsByTagName('*')
        if element.className is @mp3s[@currentTrack].songTitle
          element.scrollIntoView()
   
     getAllMp3s: () ->
       @$log.debug "getAllMp3s()"
       @Mp3Service.listMp3s()
        .then(
            (data) =>
                @$log.debug "Promise returned #{data.length} Mp3s"
                @mp3s = angular.copy data
                @currentIndex = 0
                @randomize(@mp3s.length)
                index = 0
                (mp.index=index++) for mp in @mp3s               
            ,
            (error) =>
                @$log.error "Unable to get Mp3: #{error}"
            )
        
            
     getMp3sByArtist: (artist) ->
       @$log.debug "getMp3sByArtist(#{artist})"
       @Mp3Service.listMp3sByArtist(artist)
        .then(
            (data) =>
                @$log.debug "Promise returned #{data.length} Mp3s"
                @mp3s = angular.copy data
                @currentIndex = 0
                @randomize(@mp3s.length)
                index = 0
                (mp.index=index++) for mp in @mp3s               
            ,
            (error) =>
                @$log.error "Unable to get Mp3: #{error}"
            )

     getMp3sByAlbum: (album) ->
       @$log.debug "getMp3sByAlbum(#{album})"
       @Mp3Service.listMp3sByAlbum(album)
        .then(
            (data) =>
                @$log.debug "Promise returned #{data.length} Mp3s"
                @mp3s = angular.copy data
                @currentIndex = 0
                @randomize(@mp3s.length)
                index = 0
                (mp.index=index++) for mp in @mp3s               
            ,
            (error) =>
                @$log.error "Unable to get Mp3: #{error}"
            )     
            
     getMp3sByGenre: (genre) ->
       @$log.debug "getMp3sByGenre(#{genre})"
       @Mp3Service.listMp3sByGenre(genre)
        .then(
            (data) =>
                @$log.debug "Promise returned #{data.length} Mp3s"
                @mp3s = angular.copy data
                @currentIndex = 0
                @randomize(@mp3s.length)
                index = 0
                (mp.index=index++) for mp in @mp3s               
            ,
            (error) =>
                @$log.error "Unable to get Mp3: #{error}"
            )            

     getAllArtists: () ->
       @$log.debug "getAllArtists()"
       @Mp3Service.listArtists()
        .then(
            (data) =>
                @$log.debug "f Promise returned #{data.length} artists"
                
                @artists = angular.copy data
                           
            ,
            (error) =>
                @$log.error "Unable to get artists: #{error}"
            )
            

     getAllAlbums: () ->
       @$log.debug "getAllAlbums()"
       @Mp3Service.listAlbums()
        .then(
            (data) =>
                @$log.debug "Promise returned #{data.length} albums"
                
                @albums = angular.copy data
                          
            ,
            (error) =>
                @$log.error "Unable to get albums: #{error}"
            )
             
     getAllGenres: () ->
       @$log.debug "getAllGenres()"
       @Mp3Service.listGenres()
        .then(
            (data) =>
                @$log.debug "Promise returned #{data.length} genres"
                
                @genres = angular.copy data
                          
            ,
            (error) =>
                @$log.error "Unable to get genres: #{error}"
            )   
                    
     playMp3:(mp3info)  ->
       @$log.debug "playMp3s()" + mp3info
       @title = mp3info.filePath
       @mp3 = mp3info
       @currentTrack= @mp3.index
       @updateTrack()
     
     randomize:(count) ->
       @$log.debug " randomize(#{count})" 
       @playlst = []
       for i in [0..count] by 1
         loop
           @tv = Math.floor((Math.random()*count)+1)
           
           break if( @tv not in @playlst)
           break if (i == count)
         @playlst.push(@tv)
         @$log.debug("ct: #{@playlst[i]} " )  
      
     controllersModule.controller('Mp3Ctrl', Mp3Ctrl)
     