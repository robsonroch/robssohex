package br.com.robson.robssohex.transportlayers;

import br.com.robson.robssohex.UserClaims;
import br.com.robson.robssohex.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserClaimsMapper {

    UserClaims toClaims(User user);
}
